package com.lxp.enrollment.interfaces.dto.response;

import java.time.LocalDateTime;

public record CancelEnrollmentResponse(
        long enrollmentId,
        String state,
        LocalDateTime enrolledAt
) {
}
