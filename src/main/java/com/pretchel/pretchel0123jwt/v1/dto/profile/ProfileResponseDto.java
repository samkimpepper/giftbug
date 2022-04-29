package com.pretchel.pretchel0123jwt.v1.dto.profile;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

public class ProfileResponseDto {
    @Getter
    @Setter
    public static class View {
        private String nickName;
        private String eventType;
        private String profileImageUrl;
        private String backgroundImageUrl;
        private LocalDateTime createDate;


    }
}
