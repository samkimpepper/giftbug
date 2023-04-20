package com.pretchel.pretchel0123jwt.modules.gift.dto;

import com.pretchel.pretchel0123jwt.modules.gift.domain.Gift;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Builder
public class GiftListDto {
    private String name;
    private int price;
    private int funded;
    private Date deadLine;
    private String giftImageUrl;
    private String link;
    private String story;

    public static GiftListDto fromGift(Gift gift) {
        return GiftListDto.builder()
                .name(gift.getName())
                .price(gift.getPrice())
                .funded(gift.getFunded())
                .deadLine(gift.getDeadLine())
                .giftImageUrl(gift.getGiftImageUrl())
                .link(gift.getLink())
                .story(gift.getStory())
                .build();
    }
}
