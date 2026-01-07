package com.lxp.recommend.domain.model;

import com.lxp.recommend.domain.model.ids.CourseId;
import com.lxp.recommend.domain.model.ids.EnrollmentStatus;

/**
 * 학습 이력 (Domain Model)
 * - 인프라 DTO 대신 사용하는 순수 도메인 객체
 */
public record LearningHistory(
        CourseId courseId,
        EnrollmentStatus status
) {
}
