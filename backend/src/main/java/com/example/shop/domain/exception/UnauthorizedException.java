package com.example.shop.domain.exception;

public class UnauthorizedException extends AppException {
    public UnauthorizedException(ErrorCode errorCode) {
        super(errorCode);
    }
}
