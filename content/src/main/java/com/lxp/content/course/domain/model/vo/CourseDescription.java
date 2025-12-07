package com.lxp.content.course.domain.model.vo;

public record CourseDescription(String value) {
    private static final int MAX_LENGTH = 50;

    public CourseDescription {
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Description length must be between 0 and " + MAX_LENGTH);
        }
    }

    public static CourseDescription of(String value) {
        return new CourseDescription(value);
    }
}