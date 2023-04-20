package com.pretchel.pretchel0123jwt.modules.account.exception;

import lombok.Getter;

@Getter
public class InvalidTokenException extends RuntimeException {
    private final String code;

    public InvalidTokenException() {
        super();
        code = "INVALID_TOKEN";
    }
}
