package com.lxp.enrollment.interfaces.dto.response;

import java.time.LocalDateTime;

public record EnrollCourseResponse(
        long enrollmentId,
        String state,
        LocalDateTime enrolledAt
) {
}
