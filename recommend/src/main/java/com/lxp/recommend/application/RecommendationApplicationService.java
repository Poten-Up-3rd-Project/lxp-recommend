package com.lxp.recommend.application;

import com.lxp.recommend.application.dto.RecommendedCourseDto;
import com.lxp.recommend.domain.model.MemberRecommendation;
import com.lxp.recommend.domain.model.RecommendedCourse;
import com.lxp.recommend.domain.model.ids.MemberId;
import com.lxp.recommend.domain.port.*;
import com.lxp.recommend.domain.repository.MemberRecommendationRepository;
import com.lxp.recommend.domain.service.RecommendationScoringService;
import com.lxp.recommend.domain.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
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
     * [Command] 추천 재계산 (비동기)
     * 외부에서 Long ID를 받아 내부 VO로 변환 후 로직 수행
     */
    @Async
    @Transactional
    public void refreshRecommendationAsync(Long rawMemberId) {
        log.info("[추천 계산 시작] memberId={}", rawMemberId);

        try {
            // 0. VO 변환 (Long -> MemberId)
            // 내부 로직이나 Repo 호출 시에는 이 VO를 사용합니다.
            MemberId memberId = MemberId.of(rawMemberId);

            // -------------------------------------------------------
            // 1. 프로필 조회 (Member Context 의존)
            // Reader 인터페이스는 Long을 받도록 설계했으므로 rawMemberId 전달
            LearnerProfileView profile = memberProfileReader.getProfile(rawMemberId);

            // -------------------------------------------------------
            // 2. 후보 강좌 조회 (1차 필터링, Course Context 의존)
            // - 관심 태그와 레벨에 맞는 강좌 최대 100개만 조회
            List<CourseMetaView> candidates = courseMetaReader.findCandidates(
                    profile.interestTags(),
                    profile.level(),
                    100 // limit
            );

            // -------------------------------------------------------
            // 3. 학습 이력 조회 (Learning Context 의존)
            List<LearningStatusView> learningHistory = learningStatusReader.findByMemberId(rawMemberId);

            // -------------------------------------------------------
            // 4. 도메인 서비스로 점수 계산 및 Top 4 추출
            List<RecommendedCourse> topCourses = scoringService.scoreAndRank(
                    profile,
                    candidates,
                    learningHistory
            );

            // -------------------------------------------------------
            // 5. 애그리거트 저장/갱신
            // Repository는 VO(MemberId)를 사용하도록 설계됨
            MemberRecommendation recommendation = recommendationRepository
                    .findByMemberId(memberId)
                    .orElseGet(() -> new MemberRecommendation(memberId));

            recommendation.updateItems(topCourses); // 도메인 상태 변경
            recommendationRepository.save(recommendation);

            log.info("[추천 계산 완료] memberId={}, 추천 강좌 수={}", rawMemberId, topCourses.size());

        } catch (Exception e) {
            log.error("[추천 계산 실패] memberId={}, error={}", rawMemberId, e.getMessage(), e);
            // 실패 시 알림 발송 or 재시도 로직 (필요 시 추가)
        }
    }

    /**
     * [Query] UI 노출용 추천 조회 (동기)
     */
    @Transactional(readOnly = true)
    public List<RecommendedCourseDto> getTopRecommendations(Long rawMemberId) {
        // VO 변환
        MemberId memberId = MemberId.of(rawMemberId);

        return recommendationRepository.findByMemberId(memberId)
                .map(MemberRecommendation::getItems)
                .orElse(Collections.emptyList())
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 도메인 객체 -> DTO 변환
     */
    private RecommendedCourseDto toDto(RecommendedCourse course) {
        return new RecommendedCourseDto(
                course.getCourseId().getValue(), // VO -> Long 값 꺼내기
                course.getScore(),
                course.getRank()
        );
    }
}
