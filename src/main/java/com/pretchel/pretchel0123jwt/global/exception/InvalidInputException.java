package com.pretchel.pretchel0123jwt.global.exception;

import lombok.Getter;

@Getter
public class InvalidInputException extends RuntimeException {
    private final String code;

    public InvalidInputException() {
        super();
        code = "INVALID_INPUT";
    }
}
