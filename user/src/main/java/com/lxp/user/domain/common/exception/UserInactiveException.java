package com.lxp.user.domain.common.exception;

public class UserInactiveException extends UserException {
    public UserInactiveException(String message) {
        super(UserErrorCode.USER_INACTIVE, message);
    }
}
