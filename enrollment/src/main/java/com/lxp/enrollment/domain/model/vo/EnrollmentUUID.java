package com.lxp.enrollment.domain.model.vo;

public record EnrollmentUUID(String value) {
    public EnrollmentUUID {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("EnrollmentId must be not null");
        }
    }
}
