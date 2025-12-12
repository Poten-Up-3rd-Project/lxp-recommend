package com.lxp.enrollment.application.port.provided.dto.result;

import java.time.LocalDateTime;

public record CancelEnrollmentResult(
    long enrollmentId,
    String state, // ENROLLED, COMPLETED, CANCELLED
    LocalDateTime enrolledAt
) {

}
