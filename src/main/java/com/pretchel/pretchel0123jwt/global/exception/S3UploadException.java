package com.pretchel.pretchel0123jwt.global.exception;

import lombok.Getter;

@Getter
public class S3UploadException extends RuntimeException {
    private final String code;

    public S3UploadException() {
        super();
        code = "S3_UPLOAD_FAIL";
    }
}
