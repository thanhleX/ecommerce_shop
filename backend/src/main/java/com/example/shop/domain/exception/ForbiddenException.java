package com.example.shop.domain.exception;

public class ForbiddenException extends AppException {
    public ForbiddenException(ErrorCode errorCode) {
        super(errorCode);
    }
}
