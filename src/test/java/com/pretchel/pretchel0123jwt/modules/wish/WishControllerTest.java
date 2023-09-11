package com.pretchel.pretchel0123jwt.modules.wish;

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
import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class WishControllerTest {

    @Autowired
    private WishRepository wishRepository;
    @Autowired
    private UserFactory userFactory;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private AddressAccountFactory addressAccountFactory;

    @Autowired
    private EventFactory eventFactory;

    @Autowired
    private GiftFactory giftFactory;

    @Autowired
    private GiftRepository giftRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    Gift gift;

    @Before
    public void setup() throws Exception {
        userFactory.createUser("duck12@gmail.com");
        Users user = userRepository.findByEmail("duck12@gmail.com").orElseThrow();

        Address address = addressAccountFactory.createAddress("김오리", user, true);
        Account account = addressAccountFactory.createAccount("김오리", user, true);

        Event event = eventFactory.createEvent(user, "오뤼", "2023-06-10");

        //
        gift = giftFactory.createGift("이어폰", 300000, event, account, address);

    }

    @Test
    @WithMockCustomUser
    public void test_click_wish() throws Exception {
        Users user = userRepository.findByEmail("duck12@gmail.com").orElseThrow();
        Event event = eventRepository.findAllByUsers(user).get(0);
        Gift gift = giftRepository.findAllByEvent(event).get(0);

        mvc.perform(post("/api/wish")
                .param("giftId", gift.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        Wish wish = wishRepository.findAll().get(0);

        assertThat(wish.getUser(), equalTo(user));
        assertThat(wish.getGift(), equalTo(gift));
        assertThat(gift.getWishes(), equalTo(1));
    }

    @AfterEach
    public void clean() {
        wishRepository.deleteAll();
        giftRepository.deleteAll();
        eventRepository.deleteAll();
        addressAccountFactory.deleteAll();
        notificationRepository.deleteAll();
        userRepository.deleteAll();
    }
}
