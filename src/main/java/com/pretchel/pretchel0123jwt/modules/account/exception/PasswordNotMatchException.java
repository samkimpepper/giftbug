package com.pretchel.pretchel0123jwt.modules.account.exception;

import lombok.Getter;

@Getter
public class PasswordNotMatchException extends RuntimeException {
    private final String code;

    public PasswordNotMatchException() {
        super();
        code = "PASSWORD_NOT_MATCH";
    }
}
