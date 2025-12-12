package com.lxp.content.course.application.port.required;

import com.lxp.content.course.application.port.required.dto.InstructorResult;

public interface UserQueryPort {
    InstructorResult getInstructorInfo(String userId);
}
