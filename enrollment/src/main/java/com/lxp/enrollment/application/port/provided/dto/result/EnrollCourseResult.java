package com.lxp.enrollment.application.port.provided.dto.result;

import java.time.LocalDateTime;

public record EnrollCourseResult(
    long enrollmentId,
    String state, // ENROLLED, COMPLETED, CANCELLED
    LocalDateTime enrolledAt
) {

}
