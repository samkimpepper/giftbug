package com.pretchel.pretchel0123jwt.modules.event.dto.event;

import com.pretchel.pretchel0123jwt.modules.event.domain.Event;
import com.pretchel.pretchel0123jwt.modules.gift.dto.GiftListDto;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;
import java.util.List;

@Getter
@Builder
public class EventDetailDto {
    private String nickname;
    private String eventType;
    private String profileImageUrl;
    private String backgroundImageUrl;
    private Date deadLine;
    private Boolean isExpired;
    private List<GiftListDto> giftList;

    public static EventDetailDto fromEvent(Event event, List<GiftListDto> giftList) {
        return EventDetailDto.builder()
                .nickname(event.getNickname())
                .eventType(event.getEventType())
                .profileImageUrl(event.getProfileImageUrl())
                .backgroundImageUrl(event.getBackgroundImageUrl())
                .deadLine(event.getDeadLine())
                .isExpired(event.getIsExpired())
                .giftList(giftList)
                .build();
    }

}
