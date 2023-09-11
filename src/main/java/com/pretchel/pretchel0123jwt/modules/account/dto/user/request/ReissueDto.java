package com.pretchel.pretchel0123jwt.modules.account.dto.user.request;

import lombok.Getter;

import javax.validation.constraints.NotEmpty;

@Getter
public class ReissueDto {
    @NotEmpty
    private String accessToken;
    @NotEmpty
    private String refreshToken;
}
