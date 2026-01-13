package com.lxp.recommend.infrastructure.web.dto.response;

import com.lxp.recommend.application.dto.RecommendedCourseDto;

/**
 * External API 응답용 - 추천 강좌 정보
 */
public record RecommendedCourseResponse(
        String courseId,
        double score,
        int rank
) {
    /**
     * Application DTO → Response DTO 변환
     */
    public static RecommendedCourseResponse from(RecommendedCourseDto dto) {
        return new RecommendedCourseResponse(
                dto.courseId(),
                dto.score(),
                dto.rank()
        );
    }
}
