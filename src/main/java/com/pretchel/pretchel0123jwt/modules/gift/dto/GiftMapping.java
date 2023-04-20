package com.pretchel.pretchel0123jwt.modules.gift.dto;

import com.pretchel.pretchel0123jwt.modules.gift.domain.GiftState;

public interface GiftMapping {
    String getId();
    String getName();
    int getPrice();
    String getGiftImageUrl();
    String getLink();
    String getStory();
    GiftState getGiftState();
}
