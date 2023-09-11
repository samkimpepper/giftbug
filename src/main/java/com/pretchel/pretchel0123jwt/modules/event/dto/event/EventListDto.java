package com.pretchel.pretchel0123jwt.modules.event.dto.event;

import com.pretchel.pretchel0123jwt.modules.event.domain.Event;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventListDto {
    private String id;

    private String nickname;

    private String eventType;

    private String profileImageUrl;

    private String backgroundImageUrl;

    private Date deadLine;

    private LocalDateTime createDate;

    public static EventListDto fromEvent(Event event) {
        return EventListDto.builder()
                .id(event.getId())
                .nickname(event.getNickname())
                .eventType(event.getEventType())
                .profileImageUrl(event.getProfileImageUrl())
                .backgroundImageUrl(event.getBackgroundImageUrl())
                .deadLine(event.getDeadLine())
                .createDate(event.getCreateDate())
                .build();
    }
}
