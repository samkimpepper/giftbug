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
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class FindTask {

    private final GiftQdslRepository giftQdslRepository;

    private final FindTaskTransactional findTaskTransactional;
    private final GiftService giftService;

    private final ApplicationEventPublisher eventPublisher;


    public void findExpiredGifts() {
        List<Gift> gifts = giftQdslRepository.findByDeadLineFetchJoin();

        for(Gift gift: gifts) {
            try {
                giftService.expired(gift);
                eventPublisher.publishEvent(new GiftExpiredEvent(gift, gift.getEvent().getUsers()));
            } catch (Exception ex) {

            }
        }
    }

//    public void findExpiredGifts() {
//
//            List<Gift> gifts = giftQdslRepository.findByDeadLineFetchJoin();
//
//            List<CompletableFuture<Void>> futures = gifts
//                    .stream()
//                    .map(gift -> CompletableFuture.runAsync(() -> {
//                        findTaskTransactional.markGiftAsExpired(gift);
//                    }))
//                    .collect(Collectors.toList());
//
//            CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
//            CompletableFuture<Void> exceptionally = allOf.exceptionally(throwable -> {
//                log.error("exceptionally 에러:" + throwable);
//                // 예외가 발생했을 때 처리
//                return null; // 무시하고 계속 진행하도록 null을 반환
//            });
//
//            exceptionally.join();
//    }

//        public void findExpiredGifts() {
//            while(true) {
//                List<Gift> gifts = giftQdslRepository.findByDeadLineFetchJoin();
//
//                if(gifts.isEmpty()) {
//                    break;
//                }
//
//                List<CompletableFuture<Void>> futures = gifts
//                        .stream()
//                        .map(gift -> CompletableFuture.runAsync(() -> {
//                            findTaskTransactional.markGiftAsExpired(gift);
//                        }))
//                        .collect(Collectors.toList());
//
//                CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
//                CompletableFuture<Void> exceptionally = allOf.exceptionally(throwable -> {
//                    log.error("exceptionally 에러:" + throwable);
//                    // 예외가 발생했을 때 처리
//                    return null; // 무시하고 계속 진행하도록 null을 반환
//                });
//
//                exceptionally.join();
//            }




}
