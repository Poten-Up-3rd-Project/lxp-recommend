package com.lxp.enrollment.application.port.provided.dto;

public record CancelCourseResult(
    long enrollmentId,
    String state // ENROLLED, COMPLETED, CANCELLED
) {

}
