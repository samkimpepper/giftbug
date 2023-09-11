package com.pretchel.pretchel0123jwt.modules.account.dto.user.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@ApiModel(value = "비밀번호 변경 요청")
public class UpdatePasswordDto {
    @ApiModelProperty(value = "기존 비밀번호", required = true)
    private String password;

    @ApiModelProperty(value = "새 비밀번호", required = true)
    private String newPassword;

    @ApiModelProperty(value = "새 비밀번호 확인", required = true)
    private String checkPassword;
}
