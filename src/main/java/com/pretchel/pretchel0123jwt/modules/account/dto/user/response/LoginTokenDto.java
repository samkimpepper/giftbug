package com.pretchel.pretchel0123jwt.modules.account.dto.user.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
@ApiModel(value = "로그인 응답")
public class LoginTokenDto {
    @ApiModelProperty(value = "액세스 토큰", notes = "N분 간 유효")
    private String accessToken;

    public LoginTokenDto(String accessToken) {
        this.accessToken = accessToken;
    }
}
