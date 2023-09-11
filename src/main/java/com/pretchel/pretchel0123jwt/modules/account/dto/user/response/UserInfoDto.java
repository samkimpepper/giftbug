package com.pretchel.pretchel0123jwt.modules.account.dto.user.response;

import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.info.dto.account.AccountInfoDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@ApiModel("사용자 정보 응답")
public class UserInfoDto {
    @ApiModelProperty(value = "이메일")
    private String email;
    @ApiModelProperty(value = "성별", example = "MALE")
    private String gender;
    @ApiModelProperty(value = "전화번호")
    private String phoneNumber;
    @ApiModelProperty(value = "기본 계좌")
    private AccountInfoDto defaultAccount;

    public static UserInfoDto fromUser(Users user, AccountInfoDto account) {
        return UserInfoDto.builder()
                .email(user.getEmail())
                .gender(user.getGender())
                .phoneNumber(user.getPhoneNumber())
                .defaultAccount(account)
                .build();
    }
}
