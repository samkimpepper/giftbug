package com.pretchel.pretchel0123jwt.modules.scheduler.test;

import com.pretchel.pretchel0123jwt.global.ResponseDto;
import com.pretchel.pretchel0123jwt.global.exception.NotFoundException;
import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.account.repository.UserRepository;
import com.pretchel.pretchel0123jwt.modules.event.domain.Event;
import com.pretchel.pretchel0123jwt.modules.event.repository.EventRepository;
import com.pretchel.pretchel0123jwt.modules.gift.domain.Gift;
import com.pretchel.pretchel0123jwt.modules.gift.domain.GiftState;
import com.pretchel.pretchel0123jwt.modules.gift.repository.GiftRepository;
import com.pretchel.pretchel0123jwt.modules.scheduler.task.DepositTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class DepositTaskTestController {
    private final DepositTask task;
    private final EventRepository eventRepository;
    private final GiftRepository giftRepository;
    private final UserRepository userRepository;

    /* 테스트 결과는 로그로 확인 */

    @GetMapping("/test/make")
    public ResponseDto.Empty testCreateEventAntGift(@RequestParam int funded) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = userRepository.findByEmail(email).orElseThrow(NotFoundException::new);
        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            date = new Date(sdf.parse("2023-11-20").getTime());
        } catch(ParseException ex) {
            ex.printStackTrace();
        }
        Event event = Event.builder()
                .nickname("김오리")
                .eventType("생일")
                .deadLine(date)
                .users(user)
                .build();
        eventRepository.save(event);

        user = userRepository.findByEmailFetchJoinAddresses(email).orElseThrow();
        Gift gift = Gift.builder()
                .name("귀찮아")
                .price(50000)
                .link("귀찮")
                .deadLine(date)
                .giftImageUrl("ㅋㅋ")
                .funded(funded)
                .event(event)
                .state(GiftState.expired)
                .address(user.getDefaultAddress())
                .build();

        user = userRepository.findByEmailFetchJoinAccounts(email).orElseThrow();
        gift.setAccount(user.getDefaultAccount());

        giftRepository.save(gift);
        return new ResponseDto.Empty();
    }

    @GetMapping("/test/deposit")
    public ResponseDto.Empty testDepositExpiredGiftAmount() {
        task.depositExpiredGiftAmount();
        return new ResponseDto.Empty();
    }

    @GetMapping("/test/check")
    public ResponseDto.Empty testCheckDepositResult() {
        task.checkDepositResult();
        return new ResponseDto.Empty();
    }
}
