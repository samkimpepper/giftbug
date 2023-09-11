package com.pretchel.pretchel0123jwt.global.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException{
    private final String code;
    public NotFoundException() {
        super();
        code = "NOT_FOUND";
    }

    public NotFoundException(String code) {
        super();
        this.code = code;
    }
}
