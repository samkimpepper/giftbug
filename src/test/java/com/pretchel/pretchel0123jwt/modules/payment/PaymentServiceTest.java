package com.pretchel.pretchel0123jwt.modules.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pretchel.pretchel0123jwt.TestCleanup;
import com.pretchel.pretchel0123jwt.modules.account.UserFactory;
import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.account.repository.UserRepository;
import com.pretchel.pretchel0123jwt.modules.event.EventFactory;
import com.pretchel.pretchel0123jwt.modules.event.domain.Event;
import com.pretchel.pretchel0123jwt.modules.event.repository.EventRepository;
import com.pretchel.pretchel0123jwt.modules.gift.GiftFactory;
import com.pretchel.pretchel0123jwt.modules.gift.GiftService;
import com.pretchel.pretchel0123jwt.modules.gift.domain.Gift;
import com.pretchel.pretchel0123jwt.modules.gift.domain.ProcessState;
import com.pretchel.pretchel0123jwt.modules.gift.repository.GiftRepository;
import com.pretchel.pretchel0123jwt.modules.info.AddressAccountFactory;
import com.pretchel.pretchel0123jwt.modules.info.domain.Account;
import com.pretchel.pretchel0123jwt.modules.info.domain.Address;
import com.pretchel.pretchel0123jwt.modules.notification.NotificationRepository;
import com.pretchel.pretchel0123jwt.modules.payments.iamport.IamportPaymentApi;
import com.pretchel.pretchel0123jwt.modules.payments.iamport.repository.IamportPaymentRepository;
import com.pretchel.pretchel0123jwt.modules.payments.message.MessageRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest
@TestCleanup
public class PaymentServiceTest {

    @Autowired
    private UserFactory userFactory;

    @Autowired
    private EventFactory eventFactory;

    @Autowired
    private GiftFactory giftFactory;

    @Autowired
    private AddressAccountFactory addressAccountFactory;

    @Autowired
    private PaymentFactory paymentFactory;

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

    @Autowired
    private GiftService giftService;
    @BeforeEach
    public void setup() throws Exception {

        userFactory.createUser("duck12@gmail.com");
        userFactory.createUser("chick12@gmail.com");
        Users user = userRepository.findByEmail("chick12@gmail.com").orElseThrow();
        Address address = addressAccountFactory.createAddress("박병아리", user, true);
        Account account = addressAccountFactory.createAccount("박병아리", user, true);
        Event event = eventFactory.createEvent(user, "박병아리", "2023-07-20");
        giftFactory.createGift("헤드셋", 1000000, event, account, address);
    }

    @Test
    public void test_concurrent_payments_causing_deadlock() throws Exception {
        // given
        Users user = userRepository.findByEmail("chick12@gmail.com").orElseThrow();
        Event event = eventRepository.findAllByUsers(user).get(0);
        Gift gift = giftRepository.findAllByEvent(event).get(0);

        // when
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(30);
        for(int i = 0; i < 30; i++) {
            Gift finalGift = gift;
            executorService.submit(() -> {
                try {
                    paymentFactory.syncCreatePayment(10000, finalGift, user);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // then
        gift = giftRepository.findAllByEvent(event).get(0);
        assertThat(gift.getFunded(), equalTo(300000));
    }

}
