package com.lxp.recommend.application.service;

import com.lxp.recommend.application.dto.RecommendedCourseDto;
import com.lxp.recommend.application.mapper.RecommendedCourseMapper;
import com.lxp.recommend.domain.model.MemberRecommendation;
import com.lxp.recommend.domain.model.ids.MemberId;
import com.lxp.recommend.application.port.provided.persistence.MemberRecommendationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * 추천 조회 Query Service
 *
 * 책임:
 * - 추천 결과 조회
 * - Domain → DTO 변환
 *
 * 원칙:
 * - ReadOnly Transaction만 사용
 * - 상태 변경 메서드 금지
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendQueryService {

    private static final int DEFAULT_TOP_N = 10;

    private final MemberRecommendationRepository recommendationRepository;
    private final RecommendedCourseMapper recommendedCourseMapper;

    /**
     * 추천 결과 조회 (기본 10개)
     *
     * @param memberId 회원 ID
     * @return 추천 강좌 목록
     */
    @Transactional(readOnly = true)
    public List<RecommendedCourseDto> getTopRecommendations(String memberId) {
        return getTopRecommendations(memberId, DEFAULT_TOP_N);
    }

    /**
     * 추천 결과 조회 (개수 지정)
     *
     * @param memberId 회원 ID
     * @param topN 조회할 개수
     * @return 추천 강좌 목록
     */
    @Transactional(readOnly = true)
    public List<RecommendedCourseDto> getTopRecommendations(String memberId, int topN) {
        log.info("[추천 조회] memberId={}, topN={}", memberId, topN);

        MemberId memberIdObj = MemberId.of(memberId);

        // 1. Repository에서 조회
        MemberRecommendation recommendation = recommendationRepository
                .findByMemberId(memberIdObj)
                .orElse(null);

        // 2. 추천이 없으면 빈 리스트 반환
        if (recommendation == null || recommendation.isEmpty()) {
            log.info("[추천 없음] memberId={}", memberId);
            return Collections.emptyList();
        }

        // 3. Domain → DTO 변환 (Mapper 위임)
        return recommendation.getItems().stream()
                .limit(topN)
                .map(recommendedCourseMapper::toDto)
                .toList();
    }
}
