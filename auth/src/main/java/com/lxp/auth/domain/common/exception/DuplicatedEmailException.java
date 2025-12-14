package com.lxp.auth.domain.common.exception;

public class DuplicatedEmailException extends AuthException{
    public DuplicatedEmailException() {
        super(AuthErrorCode.DUPLICATE_USER_ID);
    }
}
