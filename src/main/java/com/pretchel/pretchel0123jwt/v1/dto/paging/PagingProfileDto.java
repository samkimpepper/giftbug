package com.pretchel.pretchel0123jwt.v1.dto.paging;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PagingProfileDto {
    private String nickname;
    private String eventType;
    private String profileImageUrl;
    private String backgroundImageUrl;
    private LocalDateTime createDate;

    public PagingProfileDto(String nickname, String eventType, String profileImageUrl, String backgroundImageUrl, LocalDateTime createDate) {
        this.nickname = nickname;
        this.eventType = eventType;
        this.profileImageUrl = profileImageUrl;
        this.backgroundImageUrl = backgroundImageUrl;
        this.createDate = createDate;
    }
}
