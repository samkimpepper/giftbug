package com.pretchel.pretchel0123jwt.modules.account.exception;

import lombok.Getter;

@Getter
public class UserAlreadyExistsException extends RuntimeException {
    private final String code;

    public UserAlreadyExistsException() {
        super();
        code = "USER_ALREADY_EXISTS";
    }
}
