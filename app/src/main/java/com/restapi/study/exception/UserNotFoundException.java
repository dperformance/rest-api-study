package com.restapi.study.exception;

import com.restapi.study.global.error.exception.EntityNotFoundException;
import com.restapi.study.global.error.exception.ErrorCode;

public class UserNotFoundException extends EntityNotFoundException {

    public UserNotFoundException(Long id) {
        super(id + " User Not Found ", ErrorCode.USER_NOT_FOUND);
    }
}
