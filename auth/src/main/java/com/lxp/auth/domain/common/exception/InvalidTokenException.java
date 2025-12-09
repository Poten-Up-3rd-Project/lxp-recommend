package com.lxp.auth.domain.common.exception;

public class InvalidTokenException extends AuthException {

    public InvalidTokenException() {
        super(AuthErrorCode.UNAUTHORIZED_ACCESS);
    }

    public InvalidTokenException(String message) {
        super(AuthErrorCode.UNAUTHORIZED_ACCESS, message);
    }
}
