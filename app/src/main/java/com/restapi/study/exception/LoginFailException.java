package com.restapi.study.exception;

import com.restapi.study.global.error.exception.ErrorCode;
import com.restapi.study.global.error.exception.InvalidValueException;

public class LoginFailException extends InvalidValueException {
    public LoginFailException(String email) {
        super("login fail - email : " + email, ErrorCode.EMAIL_FAIL);
    }
}
