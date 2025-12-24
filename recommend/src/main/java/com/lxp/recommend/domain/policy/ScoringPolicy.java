package com.lxp.recommend.domain.policy;

import com.lxp.recommend.domain.model.TagContext;

import java.util.Set;

/**
 * 추천 점수 계산 정책
 *
 * 책임:
 * - 태그 매칭 시 가중치 적용
 * - 정책 변경 시 서비스 코드 수정 없이 교체 가능
 */
public record ScoringPolicy(
        double explicitTagWeight,
        double implicitTagWeight
) {
    /**
     * Compact Constructor: 유효성 검증
     */
    public ScoringPolicy {
        if (explicitTagWeight < 0 || implicitTagWeight < 0) {
            throw new IllegalArgumentException("가중치는 0 이상이어야 합니다.");
        }
    }

    /**
     * 기본 정책: Implicit 태그를 더 우대
     */
    public static ScoringPolicy defaultPolicy() {
        return new ScoringPolicy(1.0, 1.5);
    }

    /**
     * 강좌 태그와 TagContext를 기반으로 점수 계산
     *
     * @param courseTags 강좌가 가진 태그 목록
     * @param tagContext 사용자의 태그 컨텍스트
     * @return 계산된 점수
     */
    public double calculateScore(Set<String> courseTags, TagContext tagContext) {
        if (courseTags == null || courseTags.isEmpty()) {
            return 0.0;
        }

        double score = 0.0;

        for (String tag : courseTags) {
            if (tagContext.isImplicit(tag)) {
                // Implicit 태그 우선 (중복 방지: isImplicit이 true면 isExplicit 체크 안 함)
                score += implicitTagWeight;
            } else if (tagContext.isExplicit(tag)) {
                score += explicitTagWeight;
            }
        }

        return score;
    }

    @Override
    public String toString() {
        return String.format("ScoringPolicy(explicit=%.1f, implicit=%.1f)",
                explicitTagWeight, implicitTagWeight);
    }
}
