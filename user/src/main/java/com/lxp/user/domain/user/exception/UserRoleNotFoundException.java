package com.lxp.user.domain.user.exception;

import com.lxp.user.domain.common.exception.UserErrorCode;
import com.lxp.user.domain.common.exception.UserException;

public class UserRoleNotFoundException extends UserException {

    public UserRoleNotFoundException() {
        super(UserErrorCode.ROLE_NOT_FOUND);
    }
}
