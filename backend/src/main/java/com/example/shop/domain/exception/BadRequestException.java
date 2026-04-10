package com.example.shop.domain.exception;

public class BadRequestException extends AppException {
    public BadRequestException(ErrorCode errorCode) {
        super(errorCode);
    }
}
