package com.pretchel.pretchel0123jwt.modules.account.dto.user.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@ApiModel(value = "회원가입 요청")
public class UserSignupDto {
    @ApiModelProperty(value = "이메일", required = true)
    private String email;

    @ApiModelProperty(value = "비밀번호", required = true)
    private String password;

    @ApiModelProperty(value = "비밀번호 확인", required = true)
    private String checkPassword;

    @ApiModelProperty(value = "생일", dataType = "String", example = "2023-08-15", required = true)
    private String birthday;

    @ApiModelProperty(value = "성별", dataType = "String", example = "MALE", required = true)
    private String gender;

    @ApiModelProperty(value = "전화번호", dataType = "String", example = "01012345678", required = true)
    private String phoneNumber;
}
