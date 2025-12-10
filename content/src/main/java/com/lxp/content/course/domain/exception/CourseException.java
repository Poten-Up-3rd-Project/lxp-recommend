package com.lxp.content.course.domain.exception;

import com.lxp.common.domain.exception.DomainException;
import com.lxp.common.domain.exception.ErrorCode;

public class CourseException extends DomainException {
    protected CourseException(ErrorCode errorCode) {
        super(errorCode);
    }

    protected CourseException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    protected CourseException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    public static CourseException sectionMinCountViolation() {
        return new CourseException(CourseErrorCode.SECTION_MIN_COUNT_VIOLATION);
    }

    public static CourseException sectionAddNotAllowed() {
        return new CourseException(CourseErrorCode.SECTION_ADD_NOT_ALLOWED);
    }

    public static CourseException sectionRemoveNotAllowed() {
        return new CourseException(CourseErrorCode.SECTION_REMOVE_NOT_ALLOWED);
    }

    public static CourseException sectionNotFound(String uuid) {
        return new CourseException(
                CourseErrorCode.SECTION_NOT_FOUND,
                String.format("Section not found: %s", uuid)
        );
    }

    public static CourseException sectionDuplicateUuid(String uuid) {
        return new CourseException(
                CourseErrorCode.SECTION_DUPLICATE_UUID,
                String.format("Duplicate section UUID: %s", uuid)
        );
    }

    public static CourseException lectureNotFound(String uuid) {
        return new CourseException(
                CourseErrorCode.LECTURE_NOT_FOUND,
                String.format("Lecture not found: %s", uuid)
        );
    }

    public static CourseException lectureDuplicateUuid(String uuid) {
        return new CourseException(
                CourseErrorCode.LECTURE_DUPLICATE_UUID,
                String.format("Duplicate lecture UUID: %s", uuid)
        );
    }

    public static CourseException courseNotFound(String uuid) {
        return new CourseException(
                CourseErrorCode.COURSE_NOT_FOUND,
                String.format("Course not found: %s", uuid)
        );
    }

    public static CourseException tagsValidationError() {
        return new CourseException(
                CourseErrorCode.TAG_VIOLATION
        );
    }
}
