package com.lxp.enrollment.application.port.provided.command;

public record CancelEnrollmentCommand(
    long enrollmentId,
    String reason
) {

}
