package com.pretchel.pretchel0123jwt.v1.event.service;

import com.pretchel.pretchel0123jwt.v1.event.domain.Account;
import com.pretchel.pretchel0123jwt.v1.event.domain.Address;
import com.pretchel.pretchel0123jwt.v1.event.domain.Event;
import com.pretchel.pretchel0123jwt.v1.event.domain.Gift;
import com.pretchel.pretchel0123jwt.v1.event.domain.GiftState;
import com.pretchel.pretchel0123jwt.v1.event.dto.gift.GiftRequestDto;
import com.pretchel.pretchel0123jwt.global.Response;
import com.pretchel.pretchel0123jwt.v1.event.repository.AccountRepository;
import com.pretchel.pretchel0123jwt.v1.event.repository.AddressRepository;
import com.pretchel.pretchel0123jwt.v1.event.repository.EventRepository;
import com.pretchel.pretchel0123jwt.v1.event.repository.GiftRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GiftService {
    private final GiftRepository giftRepository;
    private final EventRepository eventRepository;
    private final AccountRepository accountRepository;
    private final AddressRepository addressRepository;
    private final Response responseDto;

    @Transactional
    public ResponseEntity<?> save(GiftRequestDto.Save save) {
        Event event = eventRepository.getById(save.getEventId());
        Account account = accountRepository.getById(save.getAccountId());
        Address address = addressRepository.getById(save.getAddressId());

        Gift gift = Gift.builder()
                .name(save.getName())
                .price(save.getPrice())
                .link(save.getLink())
                .story(save.getStory())
                .state(GiftState.ongoing)
                .event(event)
                .account(account)
                .address(address)
                .build();

        giftRepository.save(gift);

        return responseDto.success("?????? ?????? ??????! ^^");
    }

    @Transactional
    public ResponseEntity<?> getMyGifts(String eventId) {
        Event event = eventRepository.getById(eventId);
        if(event == null) {
            responseDto.fail("???????????? ?????? ?????????", HttpStatus.NOT_FOUND);
        }

        List<Gift> gifts = giftRepository.findAllByEventId(event);
        return responseDto.success(gifts, "????????? ??????", HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> getGiftDetail(String giftId) {
        Optional<Gift> optionalGift = giftRepository.findById(giftId);
        if(optionalGift.isEmpty()) {
            responseDto.fail("???????????? ?????? ??????", HttpStatus.NOT_FOUND);
        }
        Gift gift = optionalGift.get();



        return responseDto.success(gift, "?????? ??????", HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> delete(String giftId) {
        Gift gift = giftRepository.getById(giftId);
        if(gift == null) {
            responseDto.fail("???????????? ?????? ??????", HttpStatus.NOT_FOUND);
        }

        giftRepository.delete(gift);
        return responseDto.success("????????? ????????? ?????? ????????? ?????????????????????");
    }

    @Transactional
    public ResponseEntity<?> complete(Gift gift) {
        gift.setState(GiftState.completed);
        return responseDto.success("?????? ????????? ?????? ??? ?????? ??? ?????? ?????????");
    }
}
