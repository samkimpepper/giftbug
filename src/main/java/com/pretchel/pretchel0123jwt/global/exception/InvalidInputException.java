package com.pretchel.pretchel0123jwt.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
public class InvalidInputException extends RuntimeException {
    private String code;

    public InvalidInputException() {
        super();
        code = "INVALID_INPUT";
    }
}
