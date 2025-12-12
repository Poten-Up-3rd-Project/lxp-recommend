package com.lxp.enrollment.application.usecase;

import com.lxp.enrollment.application.port.provided.dto.command.EnrollCourseCommand;
import com.lxp.enrollment.application.port.provided.dto.result.EnrollCourseResult;

public interface EnrollCourseUseCase {
    EnrollCourseResult enroll(EnrollCourseCommand command);
}
