package com.lxp.enrollment.application.port.provided.command;

public record EnrollCourseCommand(
    String userId,
    String courseId
) {

}
