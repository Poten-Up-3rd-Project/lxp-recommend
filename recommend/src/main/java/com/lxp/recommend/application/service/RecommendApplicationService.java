package com.lxp.recommend.application.service;

import com.lxp.recommend.application.dto.RecommendedCourseDto;
import com.lxp.recommend.application.mapper.CourseMetaMapper;
import com.lxp.recommend.application.mapper.LearnerProfileMapper;
import com.lxp.recommend.application.mapper.LearningHistoryMapper;
import com.lxp.recommend.application.port.required.CourseMetaQueryPort;
import com.lxp.recommend.application.port.required.LearnerProfileQueryPort;
import com.lxp.recommend.application.port.required.LearningHistoryQueryPort;
import com.lxp.recommend.application.port.required.dto.CourseMetaData;
import com.lxp.recommend.application.port.required.dto.LearnerProfileData;
import com.lxp.recommend.application.port.required.dto.LearningHistoryData;
import com.lxp.recommend.domain.dto.DifficultyLevel;
import com.lxp.recommend.domain.dto.LearnerLevel;
import com.lxp.recommend.domain.model.*;
import com.lxp.recommend.domain.model.ids.MemberId;
import com.lxp.recommend.domain.repository.MemberRecommendationRepository;
import com.lxp.recommend.domain.service.RecommendScoringService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendApplicationService {

    private static final int CANDIDATE_LIMIT = 100;
    private static final int DEFAULT_TOP_N = 10;

    private final LearnerProfileQueryPort learnerProfileQueryPort;
    private final CourseMetaQueryPort courseMetaQueryPort;
    private final LearningHistoryQueryPort learningHistoryQueryPort;

    private final MemberRecommendationRepository recommendationRepository;
    private final RecommendScoringService scoringService;

    /**
     * 추천 계산 및 저장 (배치용)
     */
    @Transactional
    public void refreshRecommendation(String rawLearnerId) {
        log.info("[추천 계산 시작] learnerId={}", rawLearnerId);

        // 1. Port를 통해 Raw Data 조회
        LearnerProfileData profileData = learnerProfileQueryPort.getProfile(rawLearnerId)
                .orElseThrow(() -> new IllegalArgumentException("학습자 프로필을 찾을 수 없습니다: " + rawLearnerId));

        List<CourseMetaData> courseDataList = courseMetaQueryPort.findByDifficulties(
                determineTargetDifficulties(profileData.learnerLevel()),
                CANDIDATE_LIMIT
        );

        // 임시: Course API 미구현으로 빈 리스트 반환 → 추천 중단
        if (courseDataList.isEmpty()) {
            log.info("[추천 계산 중단] 강좌 후보가 없습니다. (Course BC API 미구현)");
            return;
        }

        List<LearningHistoryData> historyDataList = learningHistoryQueryPort.findByLearnerId(rawLearnerId);

        // 2. Port DTO → Domain 객체 변환
        var profileView = LearnerProfileMapper.toDomain(profileData);

        List<CourseCandidate> candidates = courseDataList.stream()
                .map(CourseMetaMapper::toDomain)
                .toList();

        var historyViews = historyDataList.stream()
                .map(LearningHistoryMapper::toDomain)
                .toList();

        // 3. 도메인 컨텍스트 생성
        RecommendContext context = RecommendContext.create(
                profileView.selectedTags(),
                historyViews,
                candidates
        );

        if (!context.hasValidContext()) {
            log.info("[추천 계산 중단] 유효한 컨텍스트 없음");
            return;
        }

        // 4. 도메인 서비스 호출
        List<RecommendedCourse> scoredCourses = scoringService.scoreAndRank(
                context,
                ScoringPolicy.defaultPolicy()
        );

        if (scoredCourses.isEmpty()) {
            log.info("[추천 계산 중단] 점수 계산 결과 없음");
            return;
        }

        // 5. Aggregate 저장
        MemberId memberId = MemberId.of(rawLearnerId);
        MemberRecommendation recommendation = recommendationRepository
                .findByMemberId(memberId)
                .orElseGet(() -> new MemberRecommendation(memberId));

        recommendation.updateItems(scoredCourses);
        recommendationRepository.save(recommendation);

        log.info("[추천 계산 완료] learnerId={}, 추천 수={}", rawLearnerId, scoredCourses.size());
    }

    /**
     * 추천 결과 조회 (API용)
     *
     * @param memberId 회원 ID
     * @return 추천 강좌 목록 (Top 10)
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

        // 1. Repository에서 저장된 추천 조회
        MemberRecommendation recommendation = recommendationRepository
                .findByMemberId(memberIdObj)
                .orElse(null);

        // 2. 추천이 없으면 빈 리스트 반환
        if (recommendation == null || recommendation.isEmpty()) {
            log.info("[추천 없음] memberId={}", memberId);
            return Collections.emptyList();
        }

        // 3. Domain → DTO 변환 (Top N개만)
        return recommendation.getItems().stream()  // ← getRecommendedCourses() → getItems()
                .limit(topN)
                .map(this::toDto)
                .toList();
    }

    /**
     * Domain 객체 → DTO 변환
     */
    private RecommendedCourseDto toDto(RecommendedCourse course) {
        return new RecommendedCourseDto(
                course.getCourseId().getValue(),
                course.getScore(),
                course.getRank()  // ← reason 대신 rank
        );
    }

    /**
     * 학습자 레벨 → 타겟 난이도 결정
     */
    private Set<String> determineTargetDifficulties(String learnerLevel) {
        LearnerLevel level = LearnerLevel.valueOf(learnerLevel);

        Set<DifficultyLevel> difficulties = switch (level) {
            case JUNIOR -> Set.of(DifficultyLevel.JUNIOR, DifficultyLevel.MIDDLE);
            case MIDDLE -> Set.of(DifficultyLevel.MIDDLE, DifficultyLevel.SENIOR);
            case SENIOR -> Set.of(DifficultyLevel.SENIOR, DifficultyLevel.EXPERT);
            case EXPERT -> Set.of(DifficultyLevel.EXPERT);
        };

        return difficulties.stream()
                .map(Enum::name)
                .collect(Collectors.toSet());
    }
}
