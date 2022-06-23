package com.pretchel.pretchel0123jwt.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.CONFLICT)
public class NotFoundException extends RuntimeException{
    private String code;
    public NotFoundException() {
        super();
        code = "NOT_FOUND";
    }
}
