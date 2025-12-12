package com.lxp.enrollment.application.usecase;

import com.lxp.enrollment.application.port.provided.dto.command.CancelEnrollmentCommand;
import com.lxp.enrollment.application.port.provided.dto.result.CancelEnrollmentResult;

public interface CancelEnrollmentUseCase {
    CancelEnrollmentResult cancel(CancelEnrollmentCommand command);
}
