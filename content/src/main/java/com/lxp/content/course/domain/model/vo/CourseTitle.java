package com.lxp.content.course.domain.model.vo;

public record CourseTitle(String value) {
    private static final int MAX_LENGTH = 20;

    public CourseTitle {
        if(value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Title must not be null or empty");
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Title length must be between 0 and " + MAX_LENGTH);
        }
    }

    public static CourseTitle of(String value) {
        return new CourseTitle(value);
    }
}