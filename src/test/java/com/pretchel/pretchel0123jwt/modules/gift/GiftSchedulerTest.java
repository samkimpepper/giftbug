package com.pretchel.pretchel0123jwt.modules.gift;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pretchel.pretchel0123jwt.modules.account.dto.user.request.UserSignupDto;
import com.pretchel.pretchel0123jwt.modules.account.service.UserService;
import com.pretchel.pretchel0123jwt.modules.event.dto.event.EventCreateDto;
import com.pretchel.pretchel0123jwt.modules.event.service.EventService;
import com.pretchel.pretchel0123jwt.modules.info.dto.account.AccountCreateDto;
import com.pretchel.pretchel0123jwt.modules.info.dto.address.AddressCreateDto;
import com.pretchel.pretchel0123jwt.modules.info.service.AccountService;
import com.pretchel.pretchel0123jwt.modules.info.service.AddressService;
import com.pretchel.pretchel0123jwt.modules.payments.message.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class GiftSchedulerTest {
    @Autowired
    ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserService userService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private EventService eventService;

    @Autowired
    private GiftService giftService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private GiftSchedulerService giftSchedulerService;

    @BeforeEach
    void setup() {
        String email = "duck12@gmail.com";
        userService.signUp(makeUser());
        accountService.createAccount(makeAccount(), email);
        addressService.createAddress(makeAddress(), email);
        eventService.save(makeEvent(), email);

    }


    // Helper ============================================
    private UserSignupDto makeUser() {
        return UserSignupDto.builder()
                .email("duck12@gmail.com")
                .password("password")
                .checkPassword("password")
                .birthday("2022-08-21")
                .phoneNumber("01012345678")
                .gender("FEMALE")
                .build();
    }

    private AccountCreateDto makeAccount() {
        return AccountCreateDto.builder()
                .name("김오리")
                .accountNum("123456789012")
                .bank("신한")
                .bankCode("027")
                .birthday("1999-01-23")
                .isDefault(true)
                .build();
    }

    private AddressCreateDto makeAddress() {
        return AddressCreateDto.builder()
                .name("김오리")
                .postCode("01234")
                .roadAddress("오리로 1길 23")
                .detailAddress("오리아파트 404호")
                .phoneNum("01040404040")
                .isDefault(true)
                .build();
    }

    private EventCreateDto makeEvent() {
        return EventCreateDto.builder()
                .nickName("오리오리")
                .eventType("생일")
                .deadLine("2022-10-23")
                .build();
    }
}
