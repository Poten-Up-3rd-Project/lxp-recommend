package com.lxp.content.course.domain.exception;

import com.lxp.common.domain.exception.ErrorCode;

public enum CourseErrorCode implements ErrorCode {
    // Section
    SECTION_MIN_COUNT_VIOLATION("COURSE_001", "Course must have at least one section", "BAD_REQUEST"),
    SECTION_ADD_NOT_ALLOWED("COURSE_002", "Cannot add section after course creation", "BAD_REQUEST"),
    SECTION_REMOVE_NOT_ALLOWED("COURSE_003", "Cannot remove last section", "BAD_REQUEST"),
    SECTION_NOT_FOUND("COURSE_004", "Section not found", "NOT_FOUND"),
    SECTION_DUPLICATE_UUID("COURSE_005", "Duplicate section UUID", "CONFLICT"),

    // Lecture
    LECTURE_NOT_FOUND("COURSE_006", "Lecture not found", "NOT_FOUND"),
    LECTURE_DUPLICATE_UUID("COURSE_007", "Duplicate lecture UUID", "CONFLICT"),

    // Course
    COURSE_NOT_FOUND("COURSE_008", "Course not found", "NOT_FOUND"),
    COURSE_ALREADY_EXISTS("COURSE_009", "Course already exists", "CONFLICT"),

    // Tag
    TAG_NOT_FOUND("COURSE_010", "Tag not found", "NOT_FOUND"),
    TAG_DUPLICATE("COURSE_011", "Duplicate tag", "CONFLICT"),
    // 1개 이상 5개 이하 제약조건
    TAG_VIOLATION("COURSE_012", "Course must have at least one tag and at most five tags", "BAD_REQUEST"),
    ;

    private final String code;
    private final String message;
    private final String group;

    CourseErrorCode(String code, String message, String group) {
        this.code = code;
        this.message = message;
        this.group = group;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getGroup() {
        return "COURSE";
    }
}
