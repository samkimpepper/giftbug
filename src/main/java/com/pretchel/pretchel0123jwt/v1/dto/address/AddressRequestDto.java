package com.pretchel.pretchel0123jwt.v1.dto.address;

import lombok.Getter;
import lombok.Setter;

public class AddressRequestDto {

    @Getter
    @Setter
    public static class Save {
        private String name;
        private String postCode;
        private String roadAddress;
        private String detailAddress;
        private String phoneNum;
        private Boolean isDefault;
    }
}
