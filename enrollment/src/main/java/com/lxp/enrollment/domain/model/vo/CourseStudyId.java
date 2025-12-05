package com.lxp.enrollment.domain.model.vo;

public record CourseStudyId(String value) {
    public CourseStudyId {
        if(value == null || value.isBlank()) {
            throw new IllegalArgumentException("CourseStudyId cannot be null or blank");
        }
    }
}
