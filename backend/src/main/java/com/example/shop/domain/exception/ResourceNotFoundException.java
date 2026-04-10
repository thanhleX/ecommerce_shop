package com.example.shop.domain.exception;

public class ResourceNotFoundException extends AppException {
    public ResourceNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
