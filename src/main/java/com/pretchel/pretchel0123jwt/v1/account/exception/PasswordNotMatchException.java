package com.pretchel.pretchel0123jwt.v1.account.exception;

import lombok.Getter;

@Getter
public class PasswordNotMatchException extends RuntimeException {
    private String code;

    public PasswordNotMatchException() {
        super();
        code = "PASSWORD_NOT_MATCH";
    }
}
