package com.lxp.recommend.infrastructure.external.enrollment;


import com.lxp.recommend.domain.model.ids.EnrollmentStatus;

public record LearningStatusView(
        String memberId,
        String courseId,
        EnrollmentStatus status // Enum (별도 파일 필요)
) {}