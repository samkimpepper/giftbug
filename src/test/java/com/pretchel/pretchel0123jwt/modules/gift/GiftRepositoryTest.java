package com.pretchel.pretchel0123jwt.modules.gift;


import com.pretchel.pretchel0123jwt.config.WithMockCustomUser;
import com.pretchel.pretchel0123jwt.modules.account.UserFactory;
import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.account.repository.UserRepository;
import com.pretchel.pretchel0123jwt.modules.event.EventFactory;
import com.pretchel.pretchel0123jwt.modules.event.domain.Event;
import com.pretchel.pretchel0123jwt.modules.event.repository.EventRepository;
import com.pretchel.pretchel0123jwt.modules.gift.domain.Gift;
import com.pretchel.pretchel0123jwt.modules.gift.repository.GiftQdslRepository;
import com.pretchel.pretchel0123jwt.modules.gift.repository.GiftRepository;
import com.pretchel.pretchel0123jwt.modules.info.AddressAccountFactory;
import com.pretchel.pretchel0123jwt.modules.info.domain.Account;
import com.pretchel.pretchel0123jwt.modules.info.domain.Address;
import com.pretchel.pretchel0123jwt.modules.notification.NotificationRepository;
import com.pretchel.pretchel0123jwt.modules.payment.PaymentFactory;
import com.pretchel.pretchel0123jwt.modules.payments.iamport.repository.IamportPaymentRepository;
import com.pretchel.pretchel0123jwt.modules.payments.message.MessageRepository;
import com.pretchel.pretchel0123jwt.modules.wish.WishRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class GiftRepositoryTest {

    @Autowired
    private GiftRepository giftRepository;

    @Autowired
    private GiftQdslRepository giftQdslRepository;

    @Autowired
    private UserFactory userFactory;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventFactory eventFactory;

    @Autowired
    private AddressAccountFactory addressAccountFactory;

    @Autowired
    private GiftFactory giftFactory;

    @Autowired
    private PaymentFactory paymentFactory;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private IamportPaymentRepository paymentRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private WishRepository wishRepository;

    @Autowired
    MockMvc mvc;

    @BeforeEach
    public void setup() throws Exception {

        userFactory.createUser("duck12@gmail.com");
        Users user = userRepository.findByEmail("duck12@gmail.com").orElseThrow();

        Address address = addressAccountFactory.createAddress("김오리", user, true);
        Account account = addressAccountFactory.createAccount("김오리", user, true);

        Event event = eventFactory.createEvent(user, "오뤼", "2023-06-10");

        //
        Gift gift = giftFactory.createGift("이어폰", 300000, event, account, address);
        paymentFactory.createPayment(50000, gift, user);
        paymentFactory.createPayment(10000, gift, user);

        giftRepository.save(gift);
        userRepository.save(user);

        gift = giftFactory.createGift("마우스", 78000, event, account, address);
        paymentFactory.createPayment(10000, gift, user);
        paymentFactory.createPayment(10000, gift, user);
        paymentFactory.createPayment(10000, gift, user);
        paymentFactory.createPayment(10000, gift, user);
        giftRepository.save(gift);

        gift = giftFactory.createGift("조명", 60000, event, account, address);
        paymentFactory.createPayment(1000, gift, user);
        giftRepository.save(gift);
    }

    @Test
    public void test_get_most_popular_gifts() throws Exception {
        List<Gift> gifts = giftQdslRepository.findGiftsWithMostMessages();
        assertThat(gifts.get(0).getName(), equalTo("마우스"));
        assertThat(gifts.get(2).getName(), equalTo("조명"));
    }

    @Test
    public void test_limited_gifts() throws Exception {
        List<Gift> gifts = giftRepository.findTop1000ByOrderByIdDesc();

    }

    @Test
    @WithMockCustomUser
    public void test_most_wished_gifts() throws Exception {
        Users user = userRepository.findByEmail("duck12@gmail.com").orElseThrow();
        Event event = eventRepository.findAllByUsers(user).get(0);
        Gift gift = giftRepository.findAllByEvent(event).get(1);

        mvc.perform(post("/api/wish")
                        .param("giftId", gift.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        List<Gift> gifts = giftRepository.findAllByOrderByWishesDesc();
        assertThat(gifts.get(0).getWishes(), equalTo(1));
    }
    @AfterEach
    void clean() {
        wishRepository.deleteAll();
        messageRepository.deleteAll();
        paymentRepository.deleteAll();
        giftRepository.deleteAll();
        eventRepository.deleteAll();
        addressAccountFactory.deleteAll();
        notificationRepository.deleteAll();
        userRepository.deleteAll();
    }

}
