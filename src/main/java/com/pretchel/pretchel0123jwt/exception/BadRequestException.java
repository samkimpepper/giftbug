package com.pretchel.pretchel0123jwt.exception;

import org.hibernate.cfg.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException{
    public BadRequestException(String msg) {
        super(msg);
    }

    public BadRequestException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
