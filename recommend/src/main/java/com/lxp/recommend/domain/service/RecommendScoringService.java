package com.lxp.recommend.domain.service;

import com.lxp.common.domain.annotation.DomainService;
import com.lxp.recommend.domain.model.*;
import com.lxp.recommend.domain.model.MemberRecommendation.ScoredCourse;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

/**
 * 추천 점수 계산 도메인 서비스
 *
 * 책임:
 * - 순수 점수 계산 로직만 담당
 * - DTO에 의존하지 않음
 */
@DomainService
public class RecommendScoringService {

    private static final int DEFAULT_LIMIT = 10;

    /**
     * RecommendContext를 받아 점수 계산 및 순위 부여
     *
     * @param context 추천 컨텍스트 (필터링 완료된 후보 포함)
     * @param policy 점수 계산 정책
     * @return 점수순 정렬된 추천 강좌 리스트 (최대 10개)
     */
    public List<RecommendedCourse> scoreAndRank(RecommendContext context, ScoringPolicy policy) {
        return scoreAndRank(context, policy, DEFAULT_LIMIT);
    }

    /**
     * RecommendContext를 받아 점수 계산 및 순위 부여 (개수 제한 가능)
     *
     * @param context 추천 컨텍스트
     * @param policy 점수 계산 정책
     * @param limit 최대 반환 개수
     * @return 점수순 정렬된 추천 강좌 리스트
     */
    public List<RecommendedCourse> scoreAndRank(
            RecommendContext context,
            ScoringPolicy policy,
            int limit
    ) {
        // 유효성 검증
        if (!context.hasValidContext()) {
            return List.of();
        }

        TagContext tagContext = context.getTagContext();
        List<CourseCandidate> candidates = context.getFilteredCandidates();

        // 1. 점수 계산
        List<ScoredCourse> scoredList = candidates.stream()
                .map(candidate -> score(candidate, tagContext, policy))
                .filter(scored -> scored.score() > 0) // 관련 없는 강좌 제외
                .sorted(Comparator.comparingDouble(ScoredCourse::score).reversed()) // 점수 내림차순
                .limit(limit)
                .toList();

        // 2. 순위 부여 (1부터 시작)
        return IntStream.range(0, scoredList.size())
                .mapToObj(i -> {
                    ScoredCourse scored = scoredList.get(i);
                    return new RecommendedCourse(
                            scored.courseId(),
                            scored.score(),
                            i + 1 // 순위: 1, 2, 3, ...
                    );
                })
                .toList();
    }

    /**
     * 개별 강좌 점수 계산
     *
     * @param candidate 강좌 후보
     * @param tagContext 태그 컨텍스트
     * @param policy 점수 계산 정책
     * @return 점수가 포함된 ScoredCourse
     */
    private ScoredCourse score(
            CourseCandidate candidate,
            TagContext tagContext,
            ScoringPolicy policy
    ) {
        double score = policy.calculateScore(candidate.tags(), tagContext);
        return new ScoredCourse(candidate.courseId(), score);
    }
}
