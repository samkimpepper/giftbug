package com.pretchel.pretchel0123jwt.modules.scheduler.task;

import com.pretchel.pretchel0123jwt.modules.gift.GiftService;
import com.pretchel.pretchel0123jwt.modules.gift.domain.Gift;
import com.pretchel.pretchel0123jwt.modules.gift.repository.GiftQdslRepository;
import com.pretchel.pretchel0123jwt.modules.notification.event.GiftExpiredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FindTaskTransactional {
    private final GiftService giftService;

    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void markGiftAsExpired(Gift gift) {
        try {
            giftService.expired(gift);
            eventPublisher.publishEvent(new GiftExpiredEvent(gift, gift.getEvent().getUsers()));
        } catch (Exception ex) {
            log.error("findTask에서 예외 발생");
        }
    }
}
