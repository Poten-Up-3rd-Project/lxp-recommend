package com.lxp.auth.domain.common.exception;

public class LoginFailureException extends AuthException {

    public LoginFailureException(AuthErrorCode errorCode) {
        super(errorCode);
    }
}
