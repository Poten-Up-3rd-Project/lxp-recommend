package com.lxp.user.domain.profile.exception;

import com.lxp.user.domain.common.exception.UserErrorCode;
import com.lxp.user.domain.common.exception.UserException;

public class TagSizeConstraintViolationException extends UserException {

    public TagSizeConstraintViolationException(int min, int max) {
        super(UserErrorCode.SIZE_CONSTRAINT_VIOLATION, "태그는 최소 " + min + "개, 최대 " + max + "개까지 입력해야 합니다.");
    }
}
