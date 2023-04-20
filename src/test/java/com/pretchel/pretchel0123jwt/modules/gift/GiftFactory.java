package com.pretchel.pretchel0123jwt.modules.gift;

import com.pretchel.pretchel0123jwt.modules.event.domain.Event;
import com.pretchel.pretchel0123jwt.modules.gift.domain.Gift;
import com.pretchel.pretchel0123jwt.modules.gift.domain.GiftState;
import com.pretchel.pretchel0123jwt.modules.gift.dto.GiftCreateDto;
import com.pretchel.pretchel0123jwt.modules.gift.repository.GiftRepository;
import com.pretchel.pretchel0123jwt.modules.info.domain.Account;
import com.pretchel.pretchel0123jwt.modules.info.domain.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GiftFactory {
    @Autowired
    GiftRepository giftRepository;

    public Gift createGift(String name, int price, Event event, Account account, Address address) {
        Gift gift = Gift.builder()
                .name(name)
                .price(price)
                .deadLine(event.getDeadLine())
                .funded(0)
                .link("www.google.co.kr")
                .story("갖고싶어요.")
                .event(event)
                .account(account)
                .address(address)
                .state(GiftState.ongoing)
                .build();

        giftRepository.save(gift);
        return gift;
    }

    public GiftCreateDto createGiftDto(String name, int price, Event event, Account account, Address address){
        GiftCreateDto dto = GiftCreateDto.builder()
                .name(name)
                .price(price)
                .link("www.google.co.kr")
                .story("갖고싶어요.")
                .eventId(event.getId())
                .accountId(account.getId())
                .addressId(address.getId())
                .build();
        return dto;
    }
}
