package com.pretchel.pretchel0123jwt.modules.gift.dto;

import com.pretchel.pretchel0123jwt.modules.gift.domain.Gift;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GiftDetailDto {
    private String name;
    private int price;
    private String deadLine;
    private int funded;
    private String giftImageUrl;
    private String link;
    private String story;
    private String state;

    // TODO: account, address 정보도 리턴해야하나? 상의 필요.

    public static GiftDetailDto from(Gift gift) {
        return GiftDetailDto.builder()
                .name(gift.getName())
                .price(gift.getPrice())
                .deadLine(gift.getDeadLine().toString())
                .funded(gift.getFunded())
                .giftImageUrl(gift.getGiftImageUrl())
                .link(gift.getLink())
                .story(gift.getStory())
                .state(gift.getState().toString())
                .build();
    }
}
