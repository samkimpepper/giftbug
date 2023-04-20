package com.pretchel.pretchel0123jwt.modules.account.dto.user.response;

import lombok.Getter;

@Getter
public class LoginTokenDto {
    private String accessToken;

    public LoginTokenDto(String accessToken) {
        this.accessToken = accessToken;
    }
}
