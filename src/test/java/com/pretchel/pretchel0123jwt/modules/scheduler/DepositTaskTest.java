package com.pretchel.pretchel0123jwt.modules.scheduler;

import com.pretchel.pretchel0123jwt.TestCleanup;
import com.pretchel.pretchel0123jwt.infra.OpenbankingApi;
import com.pretchel.pretchel0123jwt.modules.account.UserFactory;
import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.account.repository.UserRepository;
import com.pretchel.pretchel0123jwt.modules.account.service.UserSettingService;
import com.pretchel.pretchel0123jwt.modules.deposit.OpenbankingDepositRepository;
import com.pretchel.pretchel0123jwt.modules.deposit.domain.OpenbankingDeposit;
import com.pretchel.pretchel0123jwt.modules.deposit.domain.OpenbankingStatus;
import com.pretchel.pretchel0123jwt.modules.deposit.dto.*;
import com.pretchel.pretchel0123jwt.modules.event.EventFactory;
import com.pretchel.pretchel0123jwt.modules.event.domain.Event;
import com.pretchel.pretchel0123jwt.modules.event.repository.EventRepository;
import com.pretchel.pretchel0123jwt.modules.gift.GiftFactory;
import com.pretchel.pretchel0123jwt.modules.gift.GiftService;
import com.pretchel.pretchel0123jwt.modules.gift.domain.Gift;
import com.pretchel.pretchel0123jwt.modules.gift.domain.GiftState;
import com.pretchel.pretchel0123jwt.modules.gift.domain.ProcessState;
import com.pretchel.pretchel0123jwt.modules.gift.repository.GiftRepository;
import com.pretchel.pretchel0123jwt.modules.info.AddressAccountFactory;
import com.pretchel.pretchel0123jwt.modules.info.domain.Account;
import com.pretchel.pretchel0123jwt.modules.info.domain.Address;
import com.pretchel.pretchel0123jwt.modules.notification.NotificationRepository;
import com.pretchel.pretchel0123jwt.modules.payment.PaymentFactory;
import com.pretchel.pretchel0123jwt.modules.payments.iamport.repository.IamportPaymentRepository;
import com.pretchel.pretchel0123jwt.modules.payments.iamport.service.IamportMessageService;
import com.pretchel.pretchel0123jwt.modules.payments.message.MessageRepository;
import com.pretchel.pretchel0123jwt.modules.scheduler.task.DepositTask;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@SpringBootTest
@TestCleanup
public class DepositTaskTest {
    @Autowired
    private DepositTask depositTask;

    @MockBean
    private OpenbankingApi openbankingApi;

    @Autowired
    private UserFactory userFactory;

    @Autowired
    private EventFactory eventFactory;

    @Autowired
    private AddressAccountFactory addressAccountFactory;

    @Autowired
    private GiftFactory giftFactory;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GiftRepository giftRepository;

    @Autowired
    private GiftService giftService;

    @Autowired
    private PaymentFactory paymentFactory;

    @Autowired
    private OpenbankingDepositRepository depositRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private IamportPaymentRepository paymentRepository;


    @BeforeEach
    public void setup() throws Exception {
        userFactory.createUser("duck12@gmail.com");
        Users user = userRepository.findByEmail("duck12@gmail.com").orElseThrow();

        Address address = addressAccountFactory.createAddress("김오리", user, true);
        Account account = addressAccountFactory.createAccount("김오리", user, true);

        Event event = eventFactory.createEvent(user, "오뤼", "2023-06-10");

        Gift gift = giftFactory.createGift("이어폰", 300000, event, account, address);
        paymentFactory.createPayment(50000, gift, user);
        giftService.expired(gift);
        assertThat(gift.getFunded(), equalTo(50000));
        giftRepository.save(gift);
        userRepository.save(user);
    }

//    @AfterEach
//    public void clean() {
//        depositRepository.deleteAll();
//        messageRepository.deleteAll();
//        paymentRepository.deleteAll();
//        giftRepository.deleteAll();
//        eventRepository.deleteAll();
//        addressAccountFactory.deleteAll();
//        notificationRepository.deleteAll();
//        userRepository.deleteAll();
//    }

    @Test
    public void deposit_to_expired_gift_success() {
        Users user = userRepository.findByEmail("duck12@gmail.com").orElseThrow();
        Event event = eventRepository.findAllByUsers(user).get(0);
        Gift gift = giftRepository.findAllByEvent(event).get(0);

        given(openbankingApi.depositAmount(any(String.class), any(String.class),any(String.class),any(String.class))).willReturn(generateOpenbankingDepositResponseDto("000"));

        depositTask.depositExpiredGiftAmount();

        gift = giftRepository.findById(gift.getId()).orElseThrow();
        OpenbankingDeposit deposit = depositRepository.findAllByGift(gift, Sort.by(Sort.Direction.DESC, "createDate")).get(0);

        assertThat(gift.getProcessState(), equalTo(ProcessState.completed));
        assertThat(deposit.getAmount(), equalTo(50000));
        assertThat(deposit.getStatus(), equalTo(OpenbankingStatus.PAID));

        assertThat(deposit.getGift().getId(), equalTo(gift.getId()));
    }

    @Test
    public void deposit_to_expired_gift_fail() {
        Users user = userRepository.findByEmail("duck12@gmail.com").orElseThrow();
        Event event = eventRepository.findAllByUsers(user).get(0);

        given(openbankingApi.depositAmount(any(String.class), any(String.class),any(String.class),any(String.class))).willReturn(generateOpenbankingDepositResponseDto("400"));

        depositTask.depositExpiredGiftAmount();

        Gift gift = giftRepository.findAllByEvent(event).get(0);
        OpenbankingDeposit deposit = depositRepository.findAllByGift(gift, Sort.by(Sort.Direction.DESC, "createDate")).get(0);

        assertThat(deposit.getStatus(), equalTo(OpenbankingStatus.UNCHECKED));
        assertThat(gift.getProcessState(), equalTo(ProcessState.check));
        assertThat(deposit.getGift().getId(), equalTo(gift.getId()));
    }

    @Test
    public void check_deposit_result_success() {
        Users user = userRepository.findByEmail("duck12@gmail.com").orElseThrow();
        Event event = eventRepository.findAllByUsers(user).get(0);

        given(openbankingApi.depositAmount(any(String.class), any(String.class),any(String.class),any(String.class))).willReturn(generateOpenbankingDepositResponseDto("400"));
        depositTask.depositExpiredGiftAmount();

        given(openbankingApi.depositResultCheck(any(OpenbankingDeposit.class))).willReturn(generateDepositResultCheckResponseDto("000"));
        depositTask.checkDepositResult();

        Gift gift = giftRepository.findAllByEvent(event).get(0);
        OpenbankingDeposit deposit = depositRepository.findAllByGift(gift, Sort.by(Sort.Direction.DESC, "createDate")).get(0);

        assertThat(deposit.getStatus(), equalTo(OpenbankingStatus.PAID));
        assertThat(gift.getProcessState(), equalTo(ProcessState.completed));
    }

    private OpenbankingDepositResponseDto generateOpenbankingDepositResponseDto(String bank_rsp_code) {
        ResListDto resList = generateResListDto(bank_rsp_code);
        List<ResListDto> resLists = new ArrayList<>();
        resLists.add(resList);

        return OpenbankingDepositResponseDto.builder()
                .api_tran_id("2ffd133a-d17a-431d-a6a5")
                .api_tran_dtm("202306161010102937")
                .rsp_code("A0000")
                .rsp_message("응답 메시지")
                .wd_bank_code_std("097")
                .wd_bank_code_sub("1230001")
                .wd_bank_name("우리은행")
                .wd_account_num_masked("000-1230000-***")
                .wd_print_content("대금입금")
                .wd_account_holder_name("이채림")
                .res_cnt("1")
                .res_list(resLists)
                .build();
    }

    private ResListDto generateResListDto(String bank_rsp_code) {
        return ResListDto.builder()
                .tran_no("1")
                .bank_tran_id("F123456789U4BC34239Z")
                .bank_tran_date("20230616")
                .bank_rsp_code(bank_rsp_code)
                .account_alias("급여계좌")
                .bank_code_std("097")
                .bank_code_sub("1230001")
                .bank_name("우리은행")
                .account_num_masked("000-1230000-***")
                .print_content("대금입금")
                .account_holder_name("김오리")
                .tran_amt("50000")
                .cms_num("93848103221")
                .build();

    }


    private DepositResultCheckResponseDto generateDepositResultCheckResponseDto(String bank_rsp_code) {
        CheckResListDto resListDto = generateCheckResListDto(bank_rsp_code);
        List<CheckResListDto> resList = new ArrayList<>();
        resList.add(resListDto);

        return DepositResultCheckResponseDto.builder()
                .api_tran_id("2ffd133a-d17a-431d-a6a5")
                .api_tran_dtm("202306161010102937")
                .rsp_code("000")
                .rsp_message("")
                .res_cnt(1)
                .res_list(resList)
                .build();
    }

    private CheckResListDto generateCheckResListDto(String bank_rsp_code) {
        return CheckResListDto.builder()
                .tran_no(1)
                .bank_tran_id("F123456789U4BC34239Z")
                .bank_tran_date("20230616")
                .bank_rsp_code(bank_rsp_code)
                .tran_amt(50000)
                .build();
    }
}

