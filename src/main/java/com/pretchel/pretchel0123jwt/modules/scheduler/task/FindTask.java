package com.pretchel.pretchel0123jwt.modules.scheduler.task;

import com.pretchel.pretchel0123jwt.modules.gift.GiftService;
import com.pretchel.pretchel0123jwt.modules.gift.domain.Gift;
import com.pretchel.pretchel0123jwt.modules.gift.domain.GiftState;
import com.pretchel.pretchel0123jwt.modules.gift.repository.GiftQdslRepository;
import com.pretchel.pretchel0123jwt.modules.gift.repository.GiftRepository;
import com.pretchel.pretchel0123jwt.modules.notification.event.GiftCompletedEvent;
import com.pretchel.pretchel0123jwt.modules.notification.event.GiftExpiredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


import javax.transaction.Transactional;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class FindTask {
    private final GiftService giftService;

    private final GiftQdslRepository giftQdslRepository;

    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void findExpiredGifts() {
        List<Gift> gifts = giftQdslRepository.findByDeadLine();

        for(Gift gift: gifts) {
            giftService.expired(gift);
            eventPublisher.publishEvent(new GiftExpiredEvent(gift, gift.getEvent().getUsers()));
        }
    }

}
