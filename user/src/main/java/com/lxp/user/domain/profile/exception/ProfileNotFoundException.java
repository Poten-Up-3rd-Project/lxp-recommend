package com.lxp.user.domain.profile.exception;

import com.lxp.user.domain.common.exception.UserErrorCode;
import com.lxp.user.domain.common.exception.UserException;

public class ProfileNotFoundException extends UserException {
    public ProfileNotFoundException() {
        super(UserErrorCode.USER_NOT_FOUND, "기존 프로필 정보를 찾을 수 없습니다.");
    }
}
