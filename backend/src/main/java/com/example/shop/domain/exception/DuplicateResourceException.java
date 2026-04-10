package com.example.shop.domain.exception;

public class DuplicateResourceException extends AppException {
    public DuplicateResourceException(ErrorCode errorCode) {
        super(errorCode);
    }
}
