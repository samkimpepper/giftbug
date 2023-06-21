package com.pretchel.pretchel0123jwt.modules.gift;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pretchel.pretchel0123jwt.config.WithMockCustomUser;
import com.pretchel.pretchel0123jwt.modules.account.UserFactory;
import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.account.repository.UserRepository;
import com.pretchel.pretchel0123jwt.modules.event.EventFactory;
import com.pretchel.pretchel0123jwt.modules.event.domain.Event;
import com.pretchel.pretchel0123jwt.modules.event.repository.EventRepository;
import com.pretchel.pretchel0123jwt.modules.gift.dto.GiftCreateDto;
import com.pretchel.pretchel0123jwt.modules.gift.repository.GiftRepository;
import com.pretchel.pretchel0123jwt.modules.info.AddressAccountFactory;
import com.pretchel.pretchel0123jwt.modules.info.domain.Account;
import com.pretchel.pretchel0123jwt.modules.info.domain.Address;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class GiftControllerTest {
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
    ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    Users user;

    @BeforeEach
    void setup() throws ParseException {
        userFactory.createUser("duck12@gmail.com");
        Address address = addressAccountFactory.createAddress("김오리", user, true);
        Account account = addressAccountFactory.createAccount("김오리", user, true);
        Event event = eventFactory.createEvent(user, "김오리", "2022-11-25");
    }

    @AfterEach
    void clean() {
        userRepository.deleteAll();
    }

    @Test
    @WithMockCustomUser
    @Transactional
    @DisplayName("")
    void createGiftSuccess() throws Exception {
        Users user = userRepository.findByEmail("duck12@gmail.com").orElseThrow();
        Address address = user.getDefaultAddress();
        Account account = user.getDefaultAccount();
        Event event = eventRepository.findAllByUsers(user).get(0);

        GiftCreateDto dto = giftFactory.createGiftDto("아이폰 무선 충전기", 45000, event, account, address);
        String content = mapper.writeValueAsString(dto);

        mvc.perform(post("/api/gift")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

    }

}
