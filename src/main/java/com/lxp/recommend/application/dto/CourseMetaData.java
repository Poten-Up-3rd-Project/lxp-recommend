package com.lxp.recommend.application.dto;

import java.util.Set;

/**
 * 강좌 메타 데이터 (Recommend BC 관점)
 *
 * 추천 점수 계산에 필요한 정보만 포함
 * (섹션/강의 구조는 제외)
 */
public record CourseMetaData(
        String courseId,
        Set<String> tags,         // 강좌 태그 (예: ["Java", "Spring"])
        String difficulty,        // "JUNIOR", "MIDDLE", "SENIOR", "EXPERT"
        boolean isPublic          // 공개 여부
) {
    /**
     * Compact Constructor: Null 방어
     */
    public CourseMetaData {
        tags = tags != null ? Set.copyOf(tags) : Set.of();
    }
}
