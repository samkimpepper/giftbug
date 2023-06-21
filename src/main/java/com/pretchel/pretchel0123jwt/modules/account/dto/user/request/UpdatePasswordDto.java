package com.pretchel.pretchel0123jwt.modules.account.dto.user.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdatePasswordDto {
    private String password;
    private String newPassword;
    private String checkPassword;
}
