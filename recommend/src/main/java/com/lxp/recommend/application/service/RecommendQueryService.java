package com.lxp.recommend.application.service; // 또는 com.lxp.recommend.application.service.query

import com.lxp.recommend.application.dto.RecommendedCourseDto;
import com.lxp.recommend.application.port.provided.persistence.MemberRecommendationRepository;
import com.lxp.recommend.domain.model.MemberRecommendation;
import com.lxp.recommend.domain.model.RecommendedCourse;
import com.lxp.recommend.domain.model.ids.MemberId;
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
 * - 추천 결과 조회 (Repository)
 * - Domain Model → Response DTO 변환
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendQueryService {

    private static final int DEFAULT_TOP_N = 10;
    private final MemberRecommendationRepository recommendationRepository;

    /**
     * 기본 상위 10개 추천 조회
     */
    @Transactional(readOnly = true)
    public List<RecommendedCourseDto> getTopRecommendations(String memberId) {
        return getTopRecommendations(memberId, DEFAULT_TOP_N);
    }

    /**
     * 추천 결과 조회 (개수 지정)
     */
    @Transactional(readOnly = true)
    public List<RecommendedCourseDto> getTopRecommendations(String memberId, int topN) {
        log.info("[추천 조회] memberId={}, topN={}", memberId, topN);

        // MemberId.of() 호출 시 모듈 에러가 난다면, domain 모듈의 exports 설정을 확인해야 합니다.
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

        // 3. Domain → DTO 변환 (내부 private 메서드 사용)
        return recommendation.getItems().stream()
                .limit(topN)
                .map(this::toDto)
                .toList();
    }

    /**
     * Domain(RecommendedCourse) → DTO(RecommendedCourseDto) 변환
     * (기존 RecommendedCourseMapper 로직 흡수)
     */
    private RecommendedCourseDto toDto(RecommendedCourse course) {
        return new RecommendedCourseDto(
                course.getCourseId().getValue(),
                course.getScore(),
                course.getRank()
        );
    }
}
