package com.lxp.recommend.application.service;

import com.lxp.recommend.application.dto.RecommendedCourseDto;
import com.lxp.recommend.application.service.command.RecommendCommandService;
import com.lxp.recommend.application.service.query.RecommendQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Recommend BC Facade Service
 *
 * 책임:
 * - Controller 진입점 역할
 * - Command/Query 위임
 *
 * 원칙:
 * - Thin Facade (비즈니스 로직 없음)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendFacadeService {

    private final RecommendCommandService commandService;
    private final RecommendQueryService queryService;

    /**
     * 추천 계산 및 저장 (Command)
     *
     * @param learnerId 학습자 ID
     */
    public void refreshRecommendation(String learnerId) {
        commandService.refreshRecommendation(learnerId);
    }

    /**
     * 추천 결과 조회 (Query)
     *
     * @param memberId 회원 ID
     * @return 추천 강좌 목록 (Top 10)
     */
    public List<RecommendedCourseDto> getTopRecommendations(String memberId) {
        return queryService.getTopRecommendations(memberId);
    }

    /**
     * 추천 결과 조회 (개수 지정)
     *
     * @param memberId 회원 ID
     * @param topN 조회할 개수
     * @return 추천 강좌 목록
     */
    public List<RecommendedCourseDto> getTopRecommendations(String memberId, int topN) {
        return queryService.getTopRecommendations(memberId, topN);
    }
}
