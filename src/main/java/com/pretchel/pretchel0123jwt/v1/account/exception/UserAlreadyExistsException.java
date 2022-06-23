package com.pretchel.pretchel0123jwt.v1.account.exception;

import lombok.Getter;

@Getter
public class UserAlreadyExistsException extends RuntimeException {
    private String code;

    public UserAlreadyExistsException() {
        super();
        code = "USER_ALREADY_EXISTS";
    }
}
