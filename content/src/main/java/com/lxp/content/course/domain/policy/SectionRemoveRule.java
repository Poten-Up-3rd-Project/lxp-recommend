package com.lxp.content.course.domain.policy;

import com.lxp.common.domain.exception.ErrorCode;
import com.lxp.common.domain.policy.BusinessRule;
import com.lxp.content.course.domain.exception.CourseErrorCode;
import com.lxp.content.course.domain.model.collection.CourseSections;

public record SectionRemoveRule(CourseSections sections) implements BusinessRule {
    private static final int MIN_SECTION_COUNT = 1;

    @Override
    public boolean isBroken() {
        return sections.size() <= MIN_SECTION_COUNT;
    }

    @Override
    public String getMessage() {
        return String.format("Cannot remove section. Minimum %d section(s) required", MIN_SECTION_COUNT);
    }

    @Override
    public ErrorCode getErrorCode() {
        return CourseErrorCode.SECTION_REMOVE_NOT_ALLOWED;
    }
}
