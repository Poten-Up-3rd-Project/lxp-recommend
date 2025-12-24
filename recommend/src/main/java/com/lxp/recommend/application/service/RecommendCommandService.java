package com.lxp.recommend.application.service;

import com.lxp.recommend.application.service.assembler.RecommendDataAssembler;
import com.lxp.recommend.application.service.policy.DifficultyPolicyService;
import com.lxp.recommend.domain.model.*;
import com.lxp.recommend.domain.model.ids.MemberId;
import com.lxp.recommend.application.port.provided.persistence.MemberRecommendationRepository;
import com.lxp.recommend.application.service.RecommendScoringService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 추천 계산 Command Service
 *
 * 책임:
 * - 추천 계산 프로세스 오케스트레이션
 * - Aggregate 생명주기 관리
 *
 * 원칙:
 * - 읽기 전용 메서드 금지 (Query는 별도 Service)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendCommandService {

    private final RecommendDataAssembler dataAssembler;
    private final DifficultyPolicyService difficultyPolicy;
    private final RecommendScoringService scoringService;
    private final MemberRecommendationRepository recommendationRepository;

    /**
     * 추천 계산 및 저장
     *
     * @param learnerId 학습자 ID
     */
    @Transactional
    public void refreshRecommendation(String learnerId) {
        log.info("[추천 계산 시작] learnerId={}", learnerId);

        // 1. 추천 계산에 필요한 데이터 수집 (Assembler 위임)
        RecommendContext context = dataAssembler.assembleContext(learnerId);

        // 2. 컨텍스트 검증
        if (!context.hasValidContext()) {
            log.info("[추천 계산 중단] 유효한 컨텍스트 없음. learnerId={}", learnerId);
            return;
        }

        // 3. 도메인 서비스 호출 (점수 계산 + 순위 결정)
        List<RecommendedCourse> scoredCourses = scoringService.scoreAndRank(
                context,
                ScoringPolicy.defaultPolicy()
        );

        if (scoredCourses.isEmpty()) {
            log.info("[추천 계산 중단] 점수 계산 결과 없음. learnerId={}", learnerId);
            return;
        }

        // 4. Aggregate 생명주기 관리
        MemberId memberId = MemberId.of(learnerId);
        MemberRecommendation recommendation = findOrCreateRecommendation(memberId);

        // 5. 불변식 검증 후 저장 (Aggregate 내부에서 검증)
        recommendation.updateItems(scoredCourses);
        recommendationRepository.save(recommendation);

        log.info("[추천 계산 완료] learnerId={}, 추천 수={}", learnerId, scoredCourses.size());
    }

    /**
     * Aggregate 조회 또는 생성
     */
    private MemberRecommendation findOrCreateRecommendation(MemberId memberId) {
        return recommendationRepository
                .findByMemberId(memberId)
                .orElseGet(() -> {
                    log.info("[신규 추천 생성] memberId={}", memberId.getValue());
                    return new MemberRecommendation(memberId);
                });
    }
}
