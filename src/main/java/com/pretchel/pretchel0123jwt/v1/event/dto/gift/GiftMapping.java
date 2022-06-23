package com.pretchel.pretchel0123jwt.v1.event.dto.gift;

import com.pretchel.pretchel0123jwt.v1.event.domain.GiftState;

public interface GiftMapping {
    String getId();
    String getName();
    int getPrice();
    String getGiftImageUrl();
    String getLink();
    String getStory();
    GiftState getGiftState();
}
