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

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class WishControllerTest {

    @Autowired
    private WishRepository wishRepository;

    @Autowired
    private WishService wishService;
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

    @BeforeEach
    public void setup() throws Exception {
        userFactory.createUser("duck12@gmail.com");
        Users user = userRepository.findByEmail("duck12@gmail.com").orElseThrow();

        Address address = addressAccountFactory.createAddress("김오리", user, true);
        Account account = addressAccountFactory.createAccount("김오리", user, true);

        Event event = eventFactory.createEvent(user, "오뤼", "2023-06-10");

        gift = giftFactory.createGift("이어폰", 300000, event, account, address);
        giftFactory.createGift("마우스", 78000, event, account, address);
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

        Wish wish = wishRepository.findAllByUserFetchJoin(user).get(0);

        assertThat(wish.getUser().getEmail(), equalTo(user.getEmail()));

        wish = wishRepository.findAllByGiftFetchJoin(gift).get(0);
        assertThat(wish.getGift().getName(), equalTo(gift.getName()));
        assertThat(wish.getGift().getWishes(), equalTo(1));

        wishService.delete(user, gift);
    }

    @Test
    @WithMockCustomUser
    public void test_my_wishlist() throws Exception {
        Users user = userRepository.findByEmail("duck12@gmail.com").orElseThrow();
        Event event = eventRepository.findAllByUsers(user).get(0);
        Gift gift = giftRepository.findAllByEvent(event).get(0);
        wishService.create(user, gift);

        mvc.perform(get("/api/wish")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1));

    }

    @Test
    @WithMockCustomUser
    public void test_unwish() throws Exception {
        Users user = userRepository.findByEmail("duck12@gmail.com").orElseThrow();
        Event event = eventRepository.findAllByUsers(user).get(0);
        Gift gift = giftRepository.findAllByEvent(event).get(0);

        mvc.perform(post("/api/wish")
                        .param("giftId", gift.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        gift = giftRepository.findById(gift.getId()).orElseThrow();
        assertThat(gift.getWishes(), equalTo(1));

        mvc.perform(delete("/api/wish")
                .param("giftId", gift.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        gift = giftRepository.findById(gift.getId()).orElseThrow();
        assertThat(gift.getWishes(), equalTo(0));

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
