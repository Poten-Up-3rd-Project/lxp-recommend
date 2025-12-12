package com.lxp.auth.domain.common.exception;

public class EmailNotFoundException extends AuthException {

    public EmailNotFoundException() {
        super(AuthErrorCode.INVALID_CREDENTIALS);
    }
}
