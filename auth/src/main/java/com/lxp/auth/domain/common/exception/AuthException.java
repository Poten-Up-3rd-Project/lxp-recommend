package com.lxp.auth.domain.common.exception;

import com.lxp.common.domain.exception.DomainException;

public class AuthException extends DomainException {

    public AuthException() {
        super(AuthErrorCode.ACCESS_DENIED);
    }

    public AuthException(AuthErrorCode errorCode) {
        super(errorCode);
    }

    public AuthException(AuthErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
