package com.pretchel.pretchel0123jwt.modules.payments.iamport.exception;

import lombok.Getter;

@Getter
public class ForgeryPaymentException extends RuntimeException {
    private String code;
    public ForgeryPaymentException() {
        super();
        code = "FORGERY_PAYMENT";
    }
}
