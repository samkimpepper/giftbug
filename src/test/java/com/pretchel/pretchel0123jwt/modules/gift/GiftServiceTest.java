package com.pretchel.pretchel0123jwt.modules.gift;

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
import com.pretchel.pretchel0123jwt.modules.info.repository.AccountRepository;
import com.pretchel.pretchel0123jwt.modules.info.repository.AddressRepository;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class GiftServiceTest {
    @InjectMocks
    private GiftService giftService;

    @Mock
    private GiftRepository giftRepository;

    @Mock
    private UserFactory userFactory;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AddressAccountFactory addressAccountFactory;

    @BeforeEach
    public void setup() throws Exception{
        Users user = Users.builder()
                .email("duck12@gmail.com")
                .password("password")
                .build();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date deadLine = new Date(sdf.parse("2023-06-30").getTime());
        Event event = Event.builder()
                        .nickname("김오리")
                        .eventType("생일")
                        .deadLine(deadLine)
                        .users(user)
                        .isExpired(false)
                        .build();

        Address address = addressAccountFactory.generateAddress("김오리", user, true);
        Account account = addressAccountFactory.generateAccount("김오리", user, true);

        given(userRepository.findByEmail("duck12@gmail.com")).willReturn(Optional.ofNullable(user));
        given(eventRepository.findAllByUsers(user).get(0)).willReturn(event);

    }

    @Test
    @WithMockCustomUser
    public void create_gift_with_account_and_address_success() {
        Users user = userRepository.findByEmail("duck12@gmail.com").orElseThrow();
        Event event = eventRepository.findAllByUsers(user).get(0);

        Address address = user.getDefaultAddress();
        Account account = user.getDefaultAccount();

        GiftCreateDto dto = generateGiftCreateDto(21000, event);
        giftService.save(dto, event, account, address);
    }

    @Test
    @WithMockCustomUser
    public void full_funded_gift_should_be_completed() {
        Users user = userRepository.findByEmail("duck12@gmail.com").orElseThrow();
        Event event = eventRepository.findAllByUsers(user).get(0);

        Address address = user.getDefaultAddress();
        Account account = user.getDefaultAccount();

        GiftCreateDto dto = generateGiftCreateDto(30000, event);
        giftService.save(dto, event, account, address);


    }

    private GiftCreateDto generateGiftCreateDto(int price, Event event) {
        return GiftCreateDto.builder()
                .name("김오리")
                .story("갖고 싶어요")
                .link("www.google.co.kr")
                .price(price)
                .eventId(event.getId())
                .build();
    }
}
