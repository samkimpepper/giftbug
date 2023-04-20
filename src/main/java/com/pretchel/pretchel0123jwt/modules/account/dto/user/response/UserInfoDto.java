package com.pretchel.pretchel0123jwt.modules.account.dto.user.response;

import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.info.dto.account.AccountInfoDto;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserInfoDto {
    private String email;
    private String gender;
    private String phoneNumber;
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
