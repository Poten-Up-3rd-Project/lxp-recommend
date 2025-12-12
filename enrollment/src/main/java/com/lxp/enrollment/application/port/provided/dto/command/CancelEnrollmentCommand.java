package com.lxp.enrollment.application.port.provided.dto.command;

public record CancelEnrollmentCommand(
    long enrollmentId,
    String reason
) {

}
