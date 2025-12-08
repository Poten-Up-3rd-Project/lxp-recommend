package com.lxp.user.domain.common.exception;

import com.lxp.common.domain.exception.DomainException;
import com.lxp.common.domain.exception.ErrorCode;

public class UserException extends DomainException {

    protected UserException(ErrorCode errorCode) {
        super(errorCode);
    }

    public UserException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
