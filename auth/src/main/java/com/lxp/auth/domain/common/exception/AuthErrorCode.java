package com.lxp.auth.domain.common.exception;

import com.lxp.common.domain.exception.ErrorCode;

public enum AuthErrorCode implements ErrorCode {

    /**
     * AUTH_001: 아이디 또는 비밀번호 불일치 (보안을 위해 모호하게 처리)
     */
    INVALID_CREDENTIALS("UNAUTHORIZED", "AUTH_001", "아이디 또는 비밀번호가 일치하지 않습니다."),

    /**
     * AUTH_002: 토큰 만료 또는 형식 오류
     */
    UNAUTHORIZED_ACCESS("UNAUTHORIZED", "AUTH_002", "유효하지 않거나 만료된 토큰입니다."),

    /**
     * AUTH_003: 권한 부족 (접근이 거부됨)
     */
    ACCESS_DENIED("FORBIDDEN", "AUTH_003", "해당 리소스에 접근할 권한이 없습니다."),

    /**
     * AUTH_004: 이미 사용 중인 사용자명/이메일
     */
    DUPLICATE_USER_ID("CONFLICT", "AUTH_004", "이미 사용 중인 사용자 ID입니다."),

    /**
     * AUTH_005: 필수 인증 정보 누락
     */
    MISSING_REQUIRED_FIELD("BAD_REQUEST", "AUTH_005", "필수 인증 정보(ID/PW)가 누락되었습니다.");

    private final String group;
    private final String code;
    private final String message;

    AuthErrorCode(String group, String code, String message) {
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
