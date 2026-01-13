package com.lxp.recommend.infrastructure.web.dto.response;

import com.lxp.recommend.application.dto.RecommendedCourseDto;

import java.util.List;

/**
 * 강좌 목록 + 총 개수 포함
 * 향후 페이징 정보 추가 가능 (pageNumber, pageSize 등)
 * Stream API로 변환 처리
 *
 */

/**
 * External API 응답용 - 추천 강좌 목록
 */
public record RecommendationListResponse(
        List<RecommendedCourseResponse> recommendations,
        int totalCount
) {
    /**
     * Application DTO List → Response DTO 변환
     */
    public static RecommendationListResponse from(List<RecommendedCourseDto> dtos) {
        List<RecommendedCourseResponse> responses = dtos.stream()
                .map(RecommendedCourseResponse::from)
                .toList();

        return new RecommendationListResponse(responses, responses.size());
    }
}

/**
 * 최종 레스폰스 구조
 *
 * {
 *   "success": true,
 *   "data": {
 *     "recommendations": [
 *       {
 *         "courseId": "course-uuid-001",
 *         "score": 85.5,
 *         "rank": 1
 *       },
 *       {
 *         "courseId": "course-uuid-002",
 *         "score": 78.3,
 *         "rank": 2
 *       }
 *     ],
 *     "totalCount": 2
 *   },
 *   "error": null,
 *   "timestamp": "2026-01-13T12:31:00"
 * }
 */