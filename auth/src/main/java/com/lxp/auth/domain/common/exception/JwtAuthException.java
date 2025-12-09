package com.lxp.auth.domain.common.exception;

public class JwtAuthException extends AuthException {

    public JwtAuthException(String message) {
        super(AuthErrorCode.UNAUTHORIZED_ACCESS, message);
    }
}
