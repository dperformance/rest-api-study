package com.restapi.study.exception;

import com.restapi.study.global.error.exception.ErrorCode;
import com.restapi.study.global.error.exception.InvalidValueException;

public class InvalidTokenException extends InvalidValueException {

    public InvalidTokenException(String token) {
        super(token, ErrorCode.INVALID_TOKEN_VALUE);
    }
}
