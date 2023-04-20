package com.pretchel.pretchel0123jwt.global.exception;

import com.pretchel.pretchel0123jwt.global.ResponseDto;
import com.pretchel.pretchel0123jwt.modules.account.exception.ExpiredTokenException;
import com.pretchel.pretchel0123jwt.modules.account.exception.InvalidTokenException;
import com.pretchel.pretchel0123jwt.modules.payments.iamport.exception.ForgeryPaymentException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = {NotFoundException.class})
    protected ResponseDto.Fail handleNotFoundException(NotFoundException ex) {
        log.error("throw NotFoundException : {}", ex);
        return new ResponseDto.Fail(ex.getCode());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {EmptyValueExistsException.class})
    protected ResponseDto.Fail handleEmptyValueExistsException(EmptyValueExistsException ex) {
        log.error("throw EmptyValueExistsException : {}", ex);
        return new ResponseDto.Fail(ex.getCode());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {InvalidInputException.class})
    protected ResponseDto.Fail handleInvalidInputException(InvalidInputException ex) {
        log.error("throw InvalidInputException: {}", ex);
        return new ResponseDto.Fail(ex.getCode());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(value = {InvalidTokenException.class})
    protected ResponseDto.Fail handleInvalidTokenException(InvalidTokenException ex) {
        log.error("throw InvalidTokenException: {}", ex);
        return new ResponseDto.Fail(ex.getCode());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(value = {ExpiredTokenException.class})
    protected ResponseDto.Fail handleExpiredTokenException(ExpiredTokenException ex) {
        log.error("throw ExpiredTokenException: {}", ex);
        return new ResponseDto.Fail(ex.getCode());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = {S3UploadException.class})
    protected ResponseDto.Fail handleS3UploadException(S3UploadException ex) {
        log.error("throw S3UploadException: {}", ex);
        return new ResponseDto.Fail(ex.getCode());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(value = {ForgeryPaymentException.class})
    protected ResponseDto.Fail handleForgeryPaymentException(ForgeryPaymentException ex) {
        log.error("throw ForgeryPaymentException: {}", ex);
        return new ResponseDto.Fail(ex.getCode());
    }
}
