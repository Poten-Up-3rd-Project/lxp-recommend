package com.lxp.user.domain.common.exception;

import com.lxp.common.domain.exception.ErrorCode;

public enum UserErrorCode implements ErrorCode {
    USER_NOT_FOUND("NOT_FOUND", "USER_001", "사용자 정보를 찾을 수 없습니다."),
    ROLE_NOT_FOUND("NOT_FOUND", "USER_002", "사용자 역할을 찾을 수 없습니다. (유효하지 않은 Role 이름)"),

    DUPLICATE_USERNAME("CONFLICT", "USER_003", "이미 존재하는 사용자 이름입니다."),
    INVALID_EMAIL_FORMAT("BAD_REQUEST", "USER_004", "유효하지 않은 이메일 형식입니다."),

    UNHANDLED_ERROR("INTERNAL_SERVER_ERROR", "USER_005", "사용자 처리 중 알 수 없는 서버 오류가 발생했습니다."),

    SIZE_CONSTRAINT_VIOLATION("BAD_REQUEST", "USER_006", "길이가 일치하지 않습니다.");

    private final String group;
    private final String code;
    private final String message;

    UserErrorCode(String group, String code, String message) {
        this.group = group;
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public String getGroup() {
        return this.group;
    }
}
