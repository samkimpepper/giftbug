package com.pretchel.pretchel0123jwt.v1.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;

public class UserResponseDto {

    @Builder
    @Getter
    @AllArgsConstructor
    public static class TokenInfo {
        private String grantType;
        private String accessToken;
        private String  refreshToken;
        private Long refreshTokenExpirationTime;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    public static class UserInfo {
        private String email;
        private Date birthday;
    }
}
