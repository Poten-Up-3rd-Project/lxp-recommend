package com.lxp.recommend.application.port.required.dto;

import java.util.Set;

/**
 * 학습자 프로필 데이터 (Recommend BC 관점)
 *
 * 추천 계산에 필요한 정보만 포함
 */
public record LearnerProfileData(
        String learnerId,
        String learnerLevel,      // "JUNIOR", "MIDDLE", "SENIOR", "EXPERT"
        Set<String> interestTags  // 사용자가 선택한 관심 태그
) {
    /**
     * Compact Constructor: Null 방어
     */
    public LearnerProfileData {
        interestTags = interestTags != null ? Set.copyOf(interestTags) : Set.of();
    }
}
