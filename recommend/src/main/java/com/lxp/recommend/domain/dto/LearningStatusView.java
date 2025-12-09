package com.lxp.recommend.domain.dto;


public record LearningStatusView(
        String memberId,
        String courseId,
        EnrollmentStatus status // Enum (별도 파일 필요)
) {}