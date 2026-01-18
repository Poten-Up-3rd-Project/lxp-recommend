package com.lxp.recommend.infrastructure.external.enrollment.dto;

/**
 * Enrollment BC로부터 받는 수강 이력 정보
 */
public record EnrollmentResponse(
        String learnerId,
        String courseId,
        String status  // ENROLLED, COMPLETED, CANCELLED, DROPPED
) {
}
