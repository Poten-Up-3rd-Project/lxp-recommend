package com.lxp.recommend.application;

import com.lxp.recommend.application.dto.RecommendedCourseDto;
import com.lxp.recommend.application.port.required.CourseMetaReader;
import com.lxp.recommend.application.port.required.LearningStatusReader;
import com.lxp.recommend.application.port.required.MemberProfileReader;
import com.lxp.recommend.domain.model.MemberRecommendation;
import com.lxp.recommend.domain.model.RecommendedCourse;
import com.lxp.recommend.domain.model.ids.MemberId;
import com.lxp.recommend.domain.repository.MemberRecommendationRepository;
import com.lxp.recommend.domain.service.RecommendScoringService;
import com.lxp.recommend.domain.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationApplicationService {

    private final MemberProfileReader memberProfileReader;
    private final CourseMetaReader courseMetaReader;
    private final LearningStatusReader learningStatusReader;
    private final MemberRecommendationRepository recommendationRepository;
    private final RecommendScoringService scoringService;

    /**
     * [Command] 추천 재계산 (비동기)
     * 외부에서 ID를 받아 내부 VO로 변환 후 로직 수행
     */
    @Async
    @Transactional
    public void refreshRecommendationAsync(String rawMemberId) {
        log.info("[추천 계산 시작] memberId={}", rawMemberId);

        try {
            MemberId memberId = MemberId.of(rawMemberId);

            // 1. 프로필 조회 (null 체크 필수)
            LearnerProfileView profile = memberProfileReader.getProfile(rawMemberId);
            if (profile == null) {
                log.warn("[추천 계산 중단] 프로필을 찾을 수 없음. memberId={}", rawMemberId);
                return;
            }

            // 2. 연차에 따른 타겟 난이도 결정
            Set<DifficultyLevel> targetDifficulties = determineTargetDifficulties(profile.learnerLevel());

            // 3. 1차 후보군 조회 (메모리 보호를 위해 최대 100개 정도로 제한)
            //    * Reader 인터페이스에 limit 파라미터가 없다면, 추후 추가 고려
            List<CourseMetaView> candidates = courseMetaReader.findByDifficulties(targetDifficulties, 100);
            if (candidates.isEmpty()) {
                log.info("[추천 계산 중단] 해당 난이도의 강좌 후보가 없음. difficulties={}", targetDifficulties);
                return;
            }

            // 4. 학습 이력 조회
            List<LearningStatusView> learningHistory = learningStatusReader.findByMemberId(rawMemberId);

            // 5. 점수 계산 (ScoringService 호출)
            List<RecommendedCourse> topCourses = scoringService.scoreAndRank(
                    profile,
                    candidates,
                    learningHistory
            );

            // 6. 애그리거트 저장/갱신
            MemberRecommendation recommendation = recommendationRepository
                    .findByMemberId(memberId)
                    .orElseGet(() -> new MemberRecommendation(memberId));

            recommendation.updateItems(topCourses);
            recommendationRepository.save(recommendation); // 명시적 저장 (가독성)

            log.info("[추천 계산 완료] memberId={}, 생성된 추천 수={}", rawMemberId, topCourses.size());

        } catch (Exception e) {
            // 비동기 메서드는 예외가 터지면 조용히 죽으므로, 반드시 잡아서 로그를 남겨야 함
            log.error("[추천 계산 실패] 시스템 오류 발생. memberId={}, error={}", rawMemberId, e.getMessage(), e);
        }
    }

    /**
     * [Query] UI 노출용 추천 조회 (동기)
     */
    @Transactional(readOnly = true)
    public List<RecommendedCourseDto> getTopRecommendations(String rawMemberId) {
        MemberId memberId = MemberId.of(rawMemberId);

        return recommendationRepository.findByMemberId(memberId)
                .map(MemberRecommendation::getItems)
                .orElse(Collections.emptyList())
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private RecommendedCourseDto toDto(RecommendedCourse course) {
        return new RecommendedCourseDto(
                course.getCourseId().getValue(),
                course.getScore(),
                course.getRank()
        );
    }

    private Set<DifficultyLevel> determineTargetDifficulties(LearnerLevel learnerLevel) {
        return switch (learnerLevel) {
            case JUNIOR -> Set.of(DifficultyLevel.JUNIOR, DifficultyLevel.MIDDLE);
            case MIDDLE -> Set.of(DifficultyLevel.MIDDLE, DifficultyLevel.SENIOR);
            case SENIOR -> Set.of(DifficultyLevel.SENIOR, DifficultyLevel.EXPERT);
            case EXPERT -> Set.of(DifficultyLevel.EXPERT);
        };
    }
}
