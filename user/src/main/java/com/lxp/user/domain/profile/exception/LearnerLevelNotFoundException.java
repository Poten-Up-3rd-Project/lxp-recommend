package com.lxp.user.domain.profile.exception;

import com.lxp.user.domain.common.exception.UserErrorCode;
import com.lxp.user.domain.common.exception.UserException;

public class LearnerLevelNotFoundException extends UserException {
    public LearnerLevelNotFoundException() {
        super(UserErrorCode.LEVEL_NOT_FOUND);
    }
}
