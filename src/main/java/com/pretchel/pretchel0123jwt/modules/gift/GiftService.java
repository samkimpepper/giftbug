package com.pretchel.pretchel0123jwt.modules.gift;

import com.pretchel.pretchel0123jwt.global.exception.NotFoundException;
import com.pretchel.pretchel0123jwt.modules.gift.domain.Gift;
import com.pretchel.pretchel0123jwt.modules.gift.domain.GiftState;
import com.pretchel.pretchel0123jwt.modules.gift.domain.ProcessState;
import com.pretchel.pretchel0123jwt.modules.gift.dto.GiftDetailDto;
import com.pretchel.pretchel0123jwt.modules.gift.repository.GiftQdslRepository;
import com.pretchel.pretchel0123jwt.modules.gift.repository.GiftRepository;
import com.pretchel.pretchel0123jwt.modules.info.domain.Account;
import com.pretchel.pretchel0123jwt.modules.info.domain.Address;
import com.pretchel.pretchel0123jwt.modules.event.domain.Event;
import com.pretchel.pretchel0123jwt.modules.gift.dto.GiftCreateDto;
import com.pretchel.pretchel0123jwt.modules.gift.dto.GiftListDto;
import com.pretchel.pretchel0123jwt.modules.notification.event.GiftCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GiftService {
    private final GiftRepository giftRepository;

    private final GiftQdslRepository giftQdslRepository;

    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void save(GiftCreateDto dto, Event event, Account account, Address address) {
        Gift gift = Gift.builder()
                .name(dto.getName())
                .price(dto.getPrice())
                .deadLine(event.getDeadLine())
                .funded(0)
                .link(dto.getLink())
                .story(dto.getStory())
                .state(GiftState.ongoing)
                .event(event)
                .account(account)
                .address(address)
                .build();

        giftRepository.save(gift);
    }

    @Transactional
    public List<GiftListDto> getMyGifts(Event event) {
        List<Gift> gifts = giftRepository.findAllByEventId(event);

        return gifts.stream()
                .map(gift -> {
                    return GiftListDto.fromGift(gift);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public GiftDetailDto getGiftDetail(String giftId) {
        Gift gift = giftRepository.findById(giftId).orElseThrow(NotFoundException::new);

        return GiftDetailDto.from(gift);
    }

    @Transactional
    public void delete(String giftId) {
        Gift gift = giftRepository.findById(giftId).orElseThrow(NotFoundException::new);

        giftRepository.delete(gift);
    }

    @Transactional
    public void deleteAllByEvent(Event event) {
        giftRepository.deleteAllByEvent(event);
    }

    @Transactional(readOnly = true)
    public List<GiftListDto> getMostSupportedGifts() {
        List<Gift> gifts = giftQdslRepository.findGiftsWithMostMessages();
        return gifts.stream().map(
                gift -> {
                    return GiftListDto.fromGift(gift);
                }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GiftListDto> getMostWishedGifts() {
        List<Gift> gifts = giftRepository.findAllByOrderByWishesDesc();
        return gifts.stream().map(
                gift -> {
                    return GiftListDto.fromGift(gift);
                }).collect(Collectors.toList());
    }

    @Transactional
    public void expired(Gift gift) {
        gift.changeState(GiftState.expired);
        if(gift.getFunded() < 1) {
            gift.completeProcess();
        }
    }


    public void fund(Gift gift, int amount) {
        gift.pay(amount);
        if(gift.isGranterPrice()) {
            gift.changeState(GiftState.success);
            eventPublisher.publishEvent(new GiftCompletedEvent(gift, gift.getEvent().getUsers()));
        }
        giftRepository.save(gift);
    }


    public void syncFund(Gift gift, int amount) {
        gift = giftRepository.findByIdWithPessimisticLock(gift.getId()).orElseThrow();
        gift.pay(amount);
        if(gift.isGranterPrice()) {
            gift.changeState(GiftState.success);
            eventPublisher.publishEvent(new GiftCompletedEvent(gift, gift.getEvent().getUsers()));
        }
        giftRepository.saveAndFlush(gift);
    }

    public void setProcessState(Gift gift, ProcessState state) {
        gift.changeProcessState(state);
        giftRepository.save(gift);
    }

    @Transactional
    public void finish(String giftId) {
        Gift gift = giftRepository.findById(giftId).orElseThrow(NotFoundException::new);
        gift.changeState(GiftState.expired);

    }

    @Transactional
    public void increaseWishCount(Gift gift) {
        gift.increaseWishesCount();
    }

    @Transactional
    public void decreaseWishCount(Gift gift) {
        gift.decreaseWishesCount();
    }
}
