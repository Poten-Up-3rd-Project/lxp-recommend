package com.lxp.recommend.application.port.required.dto;

/**
 * 학습 이력 데이터 (Recommend BC 관점)
 *
 * 수강 상태 정보
 */
public record LearningHistoryData(
        String learnerId,
        String courseId,
        String status             // "ENROLLED", "COMPLETED", "DROPPED"
) {}
