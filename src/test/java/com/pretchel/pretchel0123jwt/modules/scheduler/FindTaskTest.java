package com.pretchel.pretchel0123jwt.modules.scheduler;

import com.pretchel.pretchel0123jwt.modules.account.UserFactory;
import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.account.repository.UserRepository;
import com.pretchel.pretchel0123jwt.modules.account.service.UserService;
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
import com.pretchel.pretchel0123jwt.modules.notification.domain.Notification;
import com.pretchel.pretchel0123jwt.modules.notification.domain.NotificationType;
import com.pretchel.pretchel0123jwt.modules.scheduler.task.FindTask;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest
public class FindTaskTest {
    @Autowired
    private FindTask findTask;

    @Autowired
    private UserFactory userFactory;

    @Autowired
    private EventFactory eventFactory;

    @Autowired
    private GiftFactory giftFactory;

    @Autowired
    private AddressAccountFactory addressAccountFactory;

    @Autowired
    private GiftService giftService;

    @Autowired
    private GiftRepository giftRepository;

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @BeforeEach
    public void setup() throws Exception {
        userFactory.createUser("duck12@gmail.com");
        Users user = userRepository.findByEmail("duck12@gmail.com").orElseThrow();

        Address address = addressAccountFactory.createAddress("김오리", user, true);
        Account account = addressAccountFactory.createAccount("김오리", user, true);

        // TODO: 오늘날짜로 설정 어떻게 하지
        Event event = eventFactory.createEvent(user, "오뤼", "2023-06-10");

        giftFactory.createGift("이어폰", 300000, event, account, address);
    }

    @AfterEach
    public void clean() {
        giftRepository.deleteAll();
        eventRepository.deleteAll();
        addressAccountFactory.deleteAll();
        notificationRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void gift_with_partially_funding_and_past_deadline_is_expired() {
        Users user = userRepository.findByEmail("duck12@gmail.com").orElseThrow();
        Event event = eventRepository.findAllByUsers(user).get(0);
        Gift gift = giftRepository.findAllByEvent(event).get(0);
        giftService.fund(gift, 5000);

        findTask.findExpiredGifts();

        gift = giftRepository.findById(gift.getId()).orElseThrow();
        assertEquals(GiftState.expired, gift.getState());
        assertThat(gift.getProcessState(), equalTo(ProcessState.none));
        Notification notification = notificationRepository.findAllByListener(user).get(0);
        assertThat(notification.getNotificationType(), equalTo(NotificationType.GIFT_EXPIRED));
    }

    @Test
    public void gift_with_no_funding_and_past_deadline_is_expired_and_completed() {
        Users user = userRepository.findByEmail("duck12@gmail.com").orElseThrow();
        Event event = eventRepository.findAllByUsers(user).get(0);
        Gift gift = giftRepository.findAllByEvent(event).get(0);
        assertThat(gift.getFunded(), equalTo(0));

        findTask.findExpiredGifts();
        gift = giftRepository.findById(gift.getId()).orElseThrow();
        assertThat(gift.getState(), equalTo(GiftState.expired));
        assertThat(gift.getProcessState(), equalTo(ProcessState.completed));

        Notification notification = notificationRepository.findAllByListener(user).get(0);
        assertThat(notification.getNotificationType(), equalTo(NotificationType.GIFT_EXPIRED));
    }

    // TODO: 다른 곳으로 옮겨야... 어디로 옮기지;
    @Test
    public void gift_with_all_funding_is_success() {
        Users user = userRepository.findByEmail("duck12@gmail.com").orElseThrow();
        Event event = eventRepository.findAllByUsers(user).get(0);
        Gift gift = giftRepository.findAllByEvent(event).get(0);
        giftService.fund(gift, 300000);

        gift = giftRepository.findById(gift.getId()).orElseThrow();
        assertThat(gift.getState(), equalTo(GiftState.success));
        assertThat(gift.getProcessState(), equalTo(ProcessState.none));

        Notification notification = notificationRepository.findAllByListener(user).get(0);
        assertThat(notification.getNotificationType(), equalTo(NotificationType.GIFT_COMPLETED));
    }
}
