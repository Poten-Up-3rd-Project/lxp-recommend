package com.lxp.enrollment.application.port.provided;

import com.lxp.enrollment.application.port.provided.command.EnrollCourseCommand;

public interface EnrollCourseUseCase {
    void enroll(EnrollCourseCommand command);
}
