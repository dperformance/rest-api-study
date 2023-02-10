package com.restapi.study.exception;

import com.restapi.study.global.error.exception.ErrorCode;
import com.restapi.study.global.error.exception.InvalidValueException;

public class UserEmailDuplicateException extends InvalidValueException {

    public UserEmailDuplicateException(String email) {
        super(email, ErrorCode.EMAIL_DUPLICATION);
    }
}
