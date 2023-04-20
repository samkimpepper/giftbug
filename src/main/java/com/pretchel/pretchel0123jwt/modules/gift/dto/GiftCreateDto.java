package com.pretchel.pretchel0123jwt.modules.gift.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GiftCreateDto {
    private String name;
    private int price;
    //private MultipartFile Image;
    private String link;
    private String story;
    private String eventId;
    private String accountId;
    private String addressId;
}
