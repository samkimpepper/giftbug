package com.pretchel.pretchel0123jwt.config;

import com.pretchel.pretchel0123jwt.global.exception.EmptyValueExistsException;
import com.pretchel.pretchel0123jwt.global.exception.NotFoundException;
import com.pretchel.pretchel0123jwt.global.ResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {NotFoundException.class})
    protected ResponseDto.Fail handleNotFoundException(NotFoundException ex) {
        log.error("throw NotFoundException : {}", ex);
        return new ResponseDto.Fail(ex.getCode());
    }

    @ExceptionHandler(value = {EmptyValueExistsException.class})
    protected ResponseDto.Fail handleEmptyValueExistsException(EmptyValueExistsException ex) {
        log.error("throw EmptyValueExistsException : {}", ex);
        return new ResponseDto.Fail(ex.getCode());
    }
}
