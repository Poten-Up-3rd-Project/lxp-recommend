package com.lxp.enrollment.domain.exception;

import com.lxp.common.domain.exception.ErrorCode;

public enum EnrollmentErrorCode implements ErrorCode {
    CAN_NOT_CANCEL_COMPLETED_ENROLLMENT("ENROLLMENT_001", "취소된 수강은 완료할 수 없습니다.", "BAD_REQUEST"),
    CAN_NOT_COMPLETE_CANCEL_ENROLLMENT("ENROLLMENT_002", "완료된 수강은 취소할 수 없습니다.", "BAD_REQUEST"),
    CAN_NOT_CANCEL_ENROLLMENT("ENROLLMENT_003", "수강 시작 후 3일이 지나 취소할 수 없습니다.", "BAD_REQUEST"),
    NOT_FOUND_ENROLLMENT("ENROLLMENT_004", "수강 이력을 찾을 수 없습니다.", "NOT_FOUND"),
    INVALID_USER_ENROLL_FAIL("ENROLLMENT_005", "유효하지 않은 사용자는 수강 신청을 할 수 없습니다", "BAD_REQUEST"),
    INVALID_COURSE_ENROLL_FAIL("ENROLLMENT_006", "유효하지 않은 강의는 수강 신청을 할 수 없습니다", "BAD_REQUEST"),
    INVALID_USER_STATUS("ENROLLMENT_007", "잘 못된 사용자 상태 값입니다.", "BAD_REQUEST"),
    ;

    private final String code;
    private final String message;
    private final String group;

    EnrollmentErrorCode(String code, String message, String group) {
        this.code = code;
        this.message = message;
        this.group = group;
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