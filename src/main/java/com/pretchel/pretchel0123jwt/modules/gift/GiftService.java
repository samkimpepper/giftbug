package com.pretchel.pretchel0123jwt.modules.gift;

import com.pretchel.pretchel0123jwt.global.exception.NotFoundException;
import com.pretchel.pretchel0123jwt.modules.gift.domain.Gift;
import com.pretchel.pretchel0123jwt.modules.gift.domain.GiftState;
import com.pretchel.pretchel0123jwt.modules.gift.repository.GiftRepository;
import com.pretchel.pretchel0123jwt.modules.info.domain.Account;
import com.pretchel.pretchel0123jwt.modules.info.domain.Address;
import com.pretchel.pretchel0123jwt.modules.event.domain.Event;
import com.pretchel.pretchel0123jwt.global.Response;
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
    private final Response responseDto;

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
    public ResponseEntity<?> getGiftDetail(String giftId) {
        Gift gift = giftRepository.findById(giftId).orElseThrow(NotFoundException::new);

        return responseDto.success(gift, "선물 대령", HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> delete(String giftId) {
        Gift gift = giftRepository.findById(giftId).orElseThrow(NotFoundException::new);

        giftRepository.delete(gift);
        return responseDto.success("당신은 소중한 선물 하나를 죽여버렸습니다");
    }

    @Transactional
    public void deleteAllByEvent(Event event) {
        giftRepository.deleteAllByEvent(event);
    }

    @Transactional
    public void expired(Gift gift) {
        gift.changeState(GiftState.expired);
        if(gift.getFunded() < 1) {
            gift.completeProcess();
        }
    }

    @Transactional
    public void fund(Gift gift, int amount) {
        //gift = giftRepository.findById(gift.getId()).orElseThrow(NotFoundException::new);
        gift.pay(amount);
        if(gift.isGranterPrice()) {
            gift.changeState(GiftState.success);
            eventPublisher.publishEvent(new GiftCompletedEvent(gift, gift.getEvent().getUsers()));
        }
    }

    @Transactional
    public void finish(String giftId) {
        Gift gift = giftRepository.findById(giftId).orElseThrow(NotFoundException::new);
        gift.changeState(GiftState.expired);

    }
}
