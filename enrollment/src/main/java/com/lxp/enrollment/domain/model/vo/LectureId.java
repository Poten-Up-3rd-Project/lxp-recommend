package com.lxp.enrollment.domain.model.vo;

public record LectureId (String value) {
    public LectureId {
        if (value != null && value.isBlank()) {
            throw new IllegalArgumentException("LectureId cannot be null and blank");
        }
    }

    public static LectureId from(String value) {
        return new LectureId(value);
    }
}
