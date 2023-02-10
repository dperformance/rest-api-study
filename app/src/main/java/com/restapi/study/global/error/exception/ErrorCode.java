package com.restapi.study.global.error.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    // Common
    INVALID_INPUT_VALUE(400,"C001", "Invalud Input Value"),

    // Product
    PRODUCT_NOT_FOUND(404, "P001", "Product Not Found"),

    // User
    EMAIL_DUPLICATION(400, "U001", "Email is Duplication");
    private int status;

    private String code;

    private String message;

    ErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

}
