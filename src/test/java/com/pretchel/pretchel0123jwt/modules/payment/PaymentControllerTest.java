package com.pretchel.pretchel0123jwt.modules.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pretchel.pretchel0123jwt.config.WithMockCustomUser;
import com.pretchel.pretchel0123jwt.modules.account.UserFactory;
import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.account.repository.UserRepository;
import com.pretchel.pretchel0123jwt.modules.event.EventFactory;
import com.pretchel.pretchel0123jwt.modules.event.domain.Event;
import com.pretchel.pretchel0123jwt.modules.event.repository.EventRepository;
import com.pretchel.pretchel0123jwt.modules.gift.GiftFactory;
import com.pretchel.pretchel0123jwt.modules.gift.domain.Gift;
import com.pretchel.pretchel0123jwt.modules.gift.repository.GiftRepository;
import com.pretchel.pretchel0123jwt.modules.info.AddressAccountFactory;
import com.pretchel.pretchel0123jwt.modules.info.domain.Account;
import com.pretchel.pretchel0123jwt.modules.info.domain.Address;
import com.pretchel.pretchel0123jwt.modules.notification.NotificationRepository;
import com.pretchel.pretchel0123jwt.modules.notification.domain.Notification;
import com.pretchel.pretchel0123jwt.modules.notification.domain.NotificationType;
import com.pretchel.pretchel0123jwt.modules.payments.iamport.IamportPaymentApi;
import com.pretchel.pretchel0123jwt.modules.payments.iamport.domain.IamportPayment;
import com.pretchel.pretchel0123jwt.modules.payments.iamport.dto.PaymentsCompleteDto;
import com.pretchel.pretchel0123jwt.modules.payments.iamport.repository.IamportPaymentRepository;
import com.pretchel.pretchel0123jwt.modules.payments.message.Message;
import com.pretchel.pretchel0123jwt.modules.payments.message.MessageRepository;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.parameters.P;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
public class PaymentControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private IamportPaymentApi iamportPaymentApi;

    @Autowired
    private UserFactory userFactory;

    @Autowired
    private EventFactory eventFactory;

    @Autowired
    private GiftFactory giftFactory;

    @Autowired
    private AddressAccountFactory addressAccountFactory;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GiftRepository giftRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private IamportPaymentRepository paymentRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private NotificationRepository notificationRepository;


    @BeforeEach
    public void setup() throws Exception {
        userFactory.createUser("duck12@gmail.com");
        userFactory.createUser("chick12@gmail.com");
        Users user = userRepository.findByEmail("chick12@gmail.com").orElseThrow();
        Address address = addressAccountFactory.createAddress("박병아리", user, true);
        Account account = addressAccountFactory.createAccount("박병아리", user, true);
        Event event = eventFactory.createEvent(user, "박병아리", "2023-07-20");
        giftFactory.createGift("헤드셋", 200000, event, account, address);
    }

    @Test
    @WithMockCustomUser
    public void payment_success() throws Exception {
        // 결제 요청에 필요한 merchant_uid부터 받기
        MvcResult result = mvc.perform(post("/test/payments/pre-create")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.merchant_uid").exists())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        JSONObject jsonResponse = new JSONObject(responseJson);
        String merchantUid = jsonResponse.getJSONObject("data").getString("merchant_uid");

        Users buyer = userRepository.findByEmail("duck12@gmail.com").orElseThrow();
        Users receiver = userRepository.findByEmail("chick12@gmail.com").orElseThrow();
        Event event = eventRepository.findAllByUsers(receiver).get(0);
        Gift gift = giftRepository.findAllByEvent(event).get(0);
        PaymentsCompleteDto dto = generatePaymentsCompleteDto(merchantUid, 5000, gift);
        String content = mapper.writeValueAsString(dto);

        given(iamportPaymentApi.checkPaymentResult(dto.getImpUid(), dto.getAmount())).willReturn(true);

        mvc.perform(post("/test/payments/complete")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        IamportPayment payment = paymentRepository.findAllByGift(gift).get(0);
        Message message = messageRepository.findByPayments(payment);
        gift = giftRepository.findById(gift.getId()).orElseThrow();
        Notification paymentCompleteNotification = notificationRepository.findAllByListener(buyer).get(0);
        Notification messageReceivedNotification = notificationRepository.findAllByListener(receiver).get(0);

        assertThat(payment.getMerchant_uid(), equalTo(merchantUid));
        assertThat(payment.getMessage(), equalTo(message.getContent()));
        assertThat(payment.getAmount(), equalTo(message.getAmount()));
        assertThat(gift.getFunded(), equalTo(5000));
        assertThat(paymentCompleteNotification.getNotificationType(), equalTo(NotificationType.PAYMENTS_COMPLETED));
        assertThat(messageReceivedNotification.getNotificationType(), equalTo(NotificationType.MESSAGE_RECEIVED));
    }

    @AfterEach
    public void clean() {
        messageRepository.deleteAll();
        paymentRepository.deleteAll();
        giftRepository.deleteAll();
        eventRepository.deleteAll();
        addressAccountFactory.deleteAll();
        notificationRepository.deleteAll();
        userRepository.deleteAll();
    }

    private PaymentsCompleteDto generatePaymentsCompleteDto(String merchantUid, int amount,  Gift gift) {
        return PaymentsCompleteDto.builder()
                .merchantUid(merchantUid)
                .impUid("머라카지")
                .amount(amount)
                .buyerName("박병아리")
                .buyerEmail("chicken@gmail.com")
                .isMember(true)
                .message("생일 축하해!")
                .giftId(gift.getId())
                .build();
    }
}
