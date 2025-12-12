package com.lxp.enrollment.application.port.provided.dto.command;

public record EnrollCourseCommand(
    String userId,
    String courseId
) { }
