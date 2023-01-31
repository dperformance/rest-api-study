package com.restapi.study.global.error.exception;

public class EntityNotFoundException extends BusinessException {
    public EntityNotFoundException(String message) {
        super(message, ErrorCode.PRODUCT_NOT_FOUND);
    }
}
