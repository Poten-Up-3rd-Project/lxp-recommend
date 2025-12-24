package com.lxp.recommend.application.service;

import com.lxp.recommend.application.mapper.CourseMetaMapper;
import com.lxp.recommend.application.mapper.LearnerProfileMapper;
import com.lxp.recommend.application.mapper.LearningHistoryMapper;
import com.lxp.recommend.application.port.required.CourseMetaQueryPort;
import com.lxp.recommend.application.port.required.LearnerProfileQueryPort;
import com.lxp.recommend.application.port.required.LearningHistoryQueryPort;
import com.lxp.recommend.application.dto.CourseMetaData;
import com.lxp.recommend.application.dto.LearnerProfileData;
import com.lxp.recommend.application.dto.LearningHistoryData;
import com.lxp.recommend.application.service.policy.DifficultyPolicyService;
import com.lxp.recommend.infrastructure.external.user.LearnerProfileView;
import com.lxp.recommend.domain.model.CourseCandidate;
import com.lxp.recommend.domain.model.RecommendContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 추천 계산용 데이터 수집 및 변환
 *
 * 책임:
 * 1. Port를 통한 외부 데이터 조회
 * 2. Port DTO → Domain 객체 변환
 * 3. RecommendContext 조립
 *
 * 원칙:
 * - 비즈니스 로직 없음 (순수 데이터 변환)
 * - Command Service의 데이터 수집 책임 위임받음
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RecommendDataAssembler {

    private static final int CANDIDATE_LIMIT = 100;

    private final LearnerProfileQueryPort learnerProfileQueryPort;
    private final CourseMetaQueryPort courseMetaQueryPort;
    private final LearningHistoryQueryPort learningHistoryQueryPort;
    private final DifficultyPolicyService difficultyPolicy;

    /**
     * 추천 계산용 컨텍스트 조립
     *
     * @param learnerId 학습자 ID
     * @return 추천 컨텍스트
     */
    public RecommendContext assembleContext(String learnerId) {
        // 1. 학습자 프로필 조회
        LearnerProfileData profileData = fetchLearnerProfile(learnerId);

        // 2. 강좌 후보 조회
        List<CourseMetaData> courseDataList = fetchCourseCandidates(profileData);

        // 3. 학습 이력 조회
        List<LearningHistoryData> historyDataList = fetchLearningHistory(learnerId);

        // 4. Port DTO → Domain 객체 변환
        LearnerProfileView profileView = LearnerProfileMapper.toDomain(profileData);

        List<CourseCandidate> candidates = courseDataList.stream()
                .map(CourseMetaMapper::toDomain)
                .toList();

        var historyViews = historyDataList.stream()
                .map(LearningHistoryMapper::toDomain)
                .toList();

        // 5. 컨텍스트 생성
        return RecommendContext.create(
                profileView.selectedTags(),
                historyViews,
                candidates
        );
    }

    /**
     * 학습자 프로필 조회
     */
    private LearnerProfileData fetchLearnerProfile(String learnerId) {
        return learnerProfileQueryPort.getProfile(learnerId)
                .orElseThrow(() -> new IllegalArgumentException("학습자 프로필을 찾을 수 없습니다: " + learnerId));
    }

    /**
     * 강좌 후보 조회
     */
    private List<CourseMetaData> fetchCourseCandidates(LearnerProfileData profileData) {
        var targetDifficulties = difficultyPolicy.determineTargetDifficulties(profileData.learnerLevel());

        List<CourseMetaData> courses = courseMetaQueryPort.findByDifficulties(
                targetDifficulties,
                CANDIDATE_LIMIT
        );

        if (courses.isEmpty()) {
            log.warn("[강좌 후보 없음] learnerLevel={} (Course BC API 미구현)", profileData.learnerLevel());
        }

        return courses;
    }

    /**
     * 학습 이력 조회
     */
    private List<LearningHistoryData> fetchLearningHistory(String learnerId) {
        List<LearningHistoryData> history = learningHistoryQueryPort.findByLearnerId(learnerId);

        if (history.isEmpty()) {
            log.debug("[학습 이력 없음] learnerId={}", learnerId);
        }

        return history;
    }
}
