package com.lxp.enrollment.domain.model.enums;

public enum EnrollmentState {
    ENROLLED("수강중"),
    COMPLETED("수강완료"),
    CANCELLED("수강취소");

    private final String description;

    EnrollmentState(String description) {
        this.description = description;
    }
}
