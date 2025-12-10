package com.lxp.content.course.domain.policy;

import com.lxp.common.domain.exception.ErrorCode;
import com.lxp.common.domain.policy.BusinessRule;
import com.lxp.content.course.domain.exception.CourseErrorCode;
import com.lxp.content.course.domain.model.collection.CourseSections;

public record SectionMinCountRule(CourseSections sections) implements BusinessRule {

    private static final int MIN_SECTION_COUNT = 1;

    @Override
    public boolean isBroken() {
        return sections == null || sections.isEmpty();
    }

    @Override
    public String getMessage() {
        return String.format("Course must have at least %d section(s)", MIN_SECTION_COUNT);
    }

    @Override
    public ErrorCode getErrorCode() {
        return CourseErrorCode.SECTION_MIN_COUNT_VIOLATION;
    }
}
