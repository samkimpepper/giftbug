package com.pretchel.pretchel0123jwt.modules.scheduler;

import com.pretchel.pretchel0123jwt.TestCleanup;
import com.pretchel.pretchel0123jwt.modules.account.UserFactory;
import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.account.repository.UserRepository;
import com.pretchel.pretchel0123jwt.modules.deposit.OpenbankingDepositRepository;
import com.pretchel.pretchel0123jwt.modules.deposit.domain.OpenbankingDeposit;
import com.pretchel.pretchel0123jwt.modules.deposit.domain.OpenbankingStatus;
import com.pretchel.pretchel0123jwt.modules.event.EventFactory;
import com.pretchel.pretchel0123jwt.modules.event.domain.Event;
import com.pretchel.pretchel0123jwt.modules.event.repository.EventRepository;
import com.pretchel.pretchel0123jwt.modules.gift.GiftFactory;
import com.pretchel.pretchel0123jwt.modules.gift.GiftService;
import com.pretchel.pretchel0123jwt.modules.gift.domain.CompletedGift;
import com.pretchel.pretchel0123jwt.modules.gift.domain.Gift;
import com.pretchel.pretchel0123jwt.modules.gift.domain.GiftState;
import com.pretchel.pretchel0123jwt.modules.gift.domain.ProcessState;
import com.pretchel.pretchel0123jwt.modules.gift.repository.CompletedGiftRepository;
import com.pretchel.pretchel0123jwt.modules.gift.repository.GiftRepository;
import com.pretchel.pretchel0123jwt.modules.info.AddressAccountFactory;
import com.pretchel.pretchel0123jwt.modules.info.domain.Account;
import com.pretchel.pretchel0123jwt.modules.info.domain.Address;
import com.pretchel.pretchel0123jwt.modules.notification.NotificationRepository;
import com.pretchel.pretchel0123jwt.modules.payment.PaymentFactory;
import com.pretchel.pretchel0123jwt.modules.payments.iamport.domain.IamportPayment;
import com.pretchel.pretchel0123jwt.modules.payments.iamport.dto.PaymentsCompleteDto;
import com.pretchel.pretchel0123jwt.modules.payments.iamport.repository.IamportPaymentRepository;
import com.pretchel.pretchel0123jwt.modules.payments.message.MessageRepository;
import com.pretchel.pretchel0123jwt.modules.scheduler.task.MoveTask;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest
@TestCleanup
public class MoveTaskTest {
    @Autowired
    private MoveTask moveTask;

    @Autowired
    private UserFactory userFactory;

    @Autowired
    private EventFactory eventFactory;

    @Autowired
    private AddressAccountFactory addressAccountFactory;

    @Autowired
    private GiftFactory giftFactory;

    @Autowired
    private PaymentFactory paymentFactory;

    @Autowired
    private DepositFactory depositFactory;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompletedGiftRepository completedGiftRepository;

    @Autowired
    private OpenbankingDepositRepository depositRepository;

    @Autowired
    private IamportPaymentRepository paymentRepository;

    /*
    * User, Address, Account, Event, Gift, Payment 만들고 Deposit도 만들고
    * Deposit status를 paid로, Gift의 process state도 완료로 수동으로 만들어야..
    * 그렇게 하고 나서 moveTask 호출.
    * */
    @BeforeEach
    public void setup() throws Exception {
        userFactory.createUser("duck12@gmail.com");
        Users receiver = userRepository.findByEmail("duck12@gmail.com").orElseThrow();
        userFactory.createUser("chick12@gmail.com");
        Users buyer = userRepository.findByEmail("chick12@gmail.com").orElseThrow();

        Address address = addressAccountFactory.createAddress("김오리", receiver, true);
        Account account = addressAccountFactory.createAccount("김오리", receiver, true);

        // TODO: 오늘날짜로 설정 어떻게 하지
        Event event = eventFactory.createEvent(receiver, "오뤼", "2023-06-20");

        // success gift, 그러나 ProcessState가 none임
        Gift gift = giftFactory.createGift("이어폰", 300000, event, account, address);
        paymentFactory.syncCreatePayment(300000, gift, buyer);

        // expired gift, ProcessState completed
        gift = giftFactory.createGift("키보드", 250000, event, account, address);
        paymentFactory.syncCreatePayment(30000, gift, buyer);
        depositFactory.createDeposit(OpenbankingStatus.PAID, 30000, gift, receiver);

    }

    @Test
    public void successful_funded_gift_moves_to_completed_gift() {
        moveTask.moveToCompletedGift();

        CompletedGift completedGift = completedGiftRepository.findAll().get(0);
        IamportPayment payment = paymentRepository.findAllByCompletedGift(completedGift).get(0);
        OpenbankingDeposit deposit = depositRepository.findAllByCompletedGift(completedGift).get(0);

        assertThat(completedGift.getName(), equalTo("키보드"));
        assertThat(payment.getGift(), equalTo(null));
        assertThat(deposit.getGift(), equalTo(null));
    }
}
