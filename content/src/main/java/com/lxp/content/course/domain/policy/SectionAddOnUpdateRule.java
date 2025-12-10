package com.lxp.content.course.domain.policy;

import com.lxp.common.domain.exception.ErrorCode;
import com.lxp.common.domain.policy.BusinessRule;
import com.lxp.content.course.domain.exception.CourseErrorCode;

public class SectionAddOnUpdateRule implements BusinessRule {

    private static final boolean ALLOW_ADD_SECTION_ON_UPDATE = false;

    @Override
    public boolean isBroken() {
        return !ALLOW_ADD_SECTION_ON_UPDATE;
    }

    @Override
    public String getMessage() {
        return "Cannot add section after course creation";
    }

    @Override
    public ErrorCode getErrorCode() {
        return CourseErrorCode.SECTION_ADD_NOT_ALLOWED;
    }
}