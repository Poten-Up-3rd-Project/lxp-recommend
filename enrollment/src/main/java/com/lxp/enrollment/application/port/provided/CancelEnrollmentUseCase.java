package com.lxp.enrollment.application.port.provided;

import com.lxp.enrollment.application.port.provided.command.CancelEnrollmentCommand;
import com.lxp.enrollment.application.port.provided.dto.CancelCourseResult;

public interface CancelEnrollmentUseCase {
    CancelCourseResult cancel(CancelEnrollmentCommand command);
}
