package com.lxp.recommend.application;



import com.lxp.recommend.application.dto.RecommendedCourseDto;
import com.lxp.recommend.domain.model.MemberRecommendation;
import com.lxp.recommend.domain.model.RecommendedCourse;
import com.example.lxp.recommendation.domain.port.CourseMetaReader;
import com.example.lxp.recommendation.domain.port.LearningStatusReader;
import com.example.lxp.recommendation.domain.port.MemberProfileReader;
import com.example.lxp.recommendation.domain.repository.MemberRecommendationRepository;
import com.example.lxp.recommendation.domain.service.RecommendationScoringService;
import com.example.lxp.recommendation.domain.support.CourseMetaView;
import com.example.lxp.recommendation.domain.support.LearnerProfileView;
import com.example.lxp.recommendation.domain.support.LearningStatusView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationApplicationService {

    // 의존성 주입 (Port & Repository & Domain Service)
    private final MemberProfileReader memberProfileReader;
    private final CourseMetaReader courseMetaReader;
    private final LearningStatusReader learningStatusReader;
    private final MemberRecommendationRepository recommendationRepository;
    private final RecommendationScoringService scoringService;

    /**
     * [Command] 추천 재계산 (비동기 권장)
     * - 회원가입 시, 프로필 수정 시, 강좌 완료 시 등 이벤트로 트리거
     * - 백그라운드에서 실행되어 UI 블로킹 없이 처리
     *
     * @param memberId 대상 회원 ID
     */
    @Async // 비동기 처리 활성화 (@EnableAsync 필요)
    @Transactional
    public void refreshRecommendationAsync(UUID memberId) {
        log.info("[추천 계산 시작] memberId={}", memberId);

        try {
            // 1. 프로필 조회 (Member Context 의존)
            LearnerProfileView profile = memberProfileReader.getProfile(memberId);

            // 2. 후보 강좌 조회 (1차 필터링, Course Context 의존)
            // - 관심 태그와 레벨에 맞는 강좌 최대 100개만 조회
            List<CourseMetaView> candidates = courseMetaReader.findCandidates(
                    profile.interestTags(),
                    profile.level(),
                    100 // limit
            );

            // 3. 학습 이력 조회 (Learning Context 의존)
            List<LearningStatusView> learningHistory = learningStatusReader.findByMemberId(memberId);

            // 4. 도메인 서비스로 점수 계산 및 Top 4 추출
            List<RecommendedCourse> topCourses = scoringService.scoreAndRank(
                    profile,
                    candidates,
                    learningHistory
            );

            // 5. 애그리거트 저장/갱신
            MemberRecommendation recommendation = recommendationRepository
                    .findByMemberId(memberId)
                    .orElseGet(() -> new MemberRecommendation(memberId));

            recommendation.updateItems(topCourses); // 도메인 메서드 호출
            recommendationRepository.save(recommendation);

            log.info("[추천 계산 완료] memberId={}, 추천 강좌 수={}", memberId, topCourses.size());

        } catch (Exception e) {
            log.error("[추천 계산 실패] memberId={}, error={}", memberId, e.getMessage(), e);
            // 실패 시 알림 발송 or 재시도 로직 추가 가능
        }
    }

    /**
     * [Query] UI 노출용 추천 조회 (동기, 매우 빠름)
     * - 이미 계산된 추천 결과를 단순 조회만 수행
     * - 결과가 없으면 빈 리스트 반환 (프론트에서 fallback 처리)
     *
     * @param memberId 대상 회원 ID
     * @return 추천 강좌 DTO 리스트 (최대 4개)
     */
    @Transactional(readOnly = true)
    public List<RecommendedCourseDto> getTopRecommendations(UUID memberId) {
        log.debug("[추천 조회] memberId={}", memberId);

        return recommendationRepository.findByMemberId(memberId)
                .map(MemberRecommendation::getItems)
                .orElse(Collections.emptyList()) // 없으면 빈 리스트
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 도메인 객체 -> DTO 변환
     */
    private RecommendedCourseDto toDto(RecommendedCourse course) {
        return new RecommendedCourseDto(
                course.getCourseId(),
                course.getScore(),
                course.getRank()
        );
    }
}
