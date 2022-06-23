package com.pretchel.pretchel0123jwt.v1.account.dto.user;

import com.pretchel.pretchel0123jwt.v1.account.domain.Users;
import com.pretchel.pretchel0123jwt.v1.event.domain.Event;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserEventsDto {
    private String nickname;
    private String eventType;
    private String profileImageUrl;
    private String backgroundImageUrl;
    private Boolean isExpired;

    public static UserEventsDto fromEvent(Event event) {
        return UserEventsDto.builder()
                .nickname(event.getNickname())
                .eventType(event.getEventType())
                .profileImageUrl(event.getProfileImageUrl())
                .backgroundImageUrl(event.getBackgroundImageUrl())
                .isExpired(event.getIsExpired())
                .build();
    }
}
