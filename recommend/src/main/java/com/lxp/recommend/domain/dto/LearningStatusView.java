package com.lxp.recommend.domain.dto;


public record LearningStatusView(
        Long memberId,
        Long courseId,
        EnrollmentStatus status // Enum (별도 파일 필요)
) {}