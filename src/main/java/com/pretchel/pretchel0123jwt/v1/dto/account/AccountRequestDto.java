package com.pretchel.pretchel0123jwt.v1.dto.account;

import lombok.Getter;
import lombok.Setter;

public class AccountRequestDto {

    @Getter
    @Setter
    public static class Save {
        private String name;
        private String accountNum;
        private String bank;
        private String bankCode;
        private String birthday;
        private Boolean isDefault;
    }
}
