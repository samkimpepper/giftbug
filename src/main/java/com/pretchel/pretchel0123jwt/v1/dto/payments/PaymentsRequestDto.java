package com.pretchel.pretchel0123jwt.v1.dto.payments;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

public class PaymentsRequestDto {

    @Getter
    @Setter
    public static class Save {
        private int amount; // 1000원 단위로만
        private String buyerName;
        private String message;
        private Boolean isMember;
        private String email;

        @NotBlank
        private String giftId;
    }

    @Getter
    @Setter
    public static class Complete {
        private String impUid;
        private String merchantUid;
        private int paidAmount;
        private String buyerName;
        private String buyerEmail; // 단순 이메일이 아니라.. 프레첼에 가입한 계정의 이메일.
    }
}
