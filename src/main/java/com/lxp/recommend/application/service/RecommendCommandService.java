package com.lxp.recommend.application.service;

import com.lxp.recommend.application.dto.CourseMetaData;
import com.lxp.recommend.application.dto.LearnerProfileData;
import com.lxp.recommend.application.dto.LearningHistoryData;
import com.lxp.recommend.application.port.required.CourseMetaQueryPort;
import com.lxp.recommend.application.port.required.LearnerProfileQueryPort;
import com.lxp.recommend.application.port.required.LearningHistoryQueryPort;
import com.lxp.recommend.application.port.provided.persistence.MemberRecommendationRepository;
import com.lxp.recommend.domain.model.*;
import com.lxp.recommend.domain.model.ids.CourseId;
import com.lxp.recommend.domain.model.ids.EnrollmentStatus;
import com.lxp.recommend.domain.model.ids.Level;  // ✅ 수정
import com.lxp.recommend.domain.model.ids.MemberId;
import com.lxp.recommend.domain.policy.ScoringPolicy;
import com.lxp.recommend.infrastructure.external.common.LevelMapper;  // ✅ 추가
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendCommandService {

    private final MemberRecommendationRepository recommendationRepository;
    private final LearnerProfileQueryPort userPort;
    private final CourseMetaQueryPort coursePort;
    private final LearningHistoryQueryPort historyPort;

    @Transactional
    public void refreshRecommendation(String learnerId) {
        log.info("[추천 계산 시작] learnerId={}", learnerId);

        // 1. 외부 데이터 수집
        RecommendContext context = assembleContext(learnerId);

        if (!context.hasValidContext()) {
            log.info("[추천 계산 중단] 유효한 컨텍스트 없음.");
            return;
        }

        // 2. 도메인 로직 (점수 계산)
        List<RecommendedCourse> scoredCourses = calculateScores(context);

        if (scoredCourses.isEmpty()) {
            log.info("[추천 계산 중단] 점수 계산 결과 없음.");
            return;
        }

        // 3. 저장
        MemberRecommendation recommendation = findOrCreateRecommendation(MemberId.of(learnerId));
        recommendation.updateItems(scoredCourses);
        recommendationRepository.save(recommendation);

        log.info("[추천 계산 완료] learnerId={}, 추천 수={}", learnerId, scoredCourses.size());
    }

    /**
     * 추천 계산용 컨텍스트 조립
     */
    private RecommendContext assembleContext(String learnerId) {
        // 1. 프로필 조회
        LearnerProfileData profile = userPort.getProfile(learnerId)
                .orElseThrow(() -> new IllegalArgumentException("학습자 프로필을 찾을 수 없습니다: " + learnerId));

        // 2. 학습 이력 조회 → Domain VO 변환
        List<LearningHistoryData> historyDtos = historyPort.findByLearnerId(learnerId);
        List<LearningHistory> histories = historyDtos.stream()
                .map(d -> new LearningHistory(
                        CourseId.of(d.courseId()),
                        EnrollmentStatus.valueOf(d.status())
                ))
                .toList();

        // 3. 학습자 레벨 기반 타겟 난이도 결정
        Level learnerLevel = Level.valueOf(profile.learnerLevel());  // ✅ 수정
        Set<Level> targetLevelEnums = LevelMapper.determineTargetLevels(learnerLevel);  // ✅ 활성화
        Set<String> targetLevelStrings = LevelMapper.toStringSet(targetLevelEnums);  // ✅ 활성화

        log.debug("[난이도 정책] learnerLevel={}, targetLevels={}", learnerLevel, targetLevelStrings);

        // 4. 후보 강좌 조회 → Domain VO 변환
        List<CourseMetaData> courseDtos = coursePort.findByDifficulties(targetLevelStrings, 100);
        List<CourseCandidate> candidates = courseDtos.stream()
                .map(d -> new CourseCandidate(
                        CourseId.of(d.courseId()),
                        d.tags(),
                        Level.valueOf(d.difficulty()),  // ✅ 수정
                        d.isPublic()
                ))
                .toList();

        log.debug("[데이터 수집 완료] 후보 강좌 수={}, 학습 이력 수={}", candidates.size(), histories.size());

        return RecommendContext.create(profile.interestTags(), histories, candidates);
    }

    /**
     * 점수 계산 및 순위 부여
     */
    private List<RecommendedCourse> calculateScores(RecommendContext context) {
        ScoringPolicy policy = ScoringPolicy.defaultPolicy();

        // 1. 점수 계산 (중간 객체 사용)
        List<ScoredItem> scoredItems = context.getFilteredCandidates().stream()
                .map(candidate -> {
                    double score = policy.calculateScore(candidate.getTags(), context.getTagContext());
                    return new ScoredItem(candidate.getCourseId(), score);
                })
                .filter(item -> item.score() > 0)
                .sorted((i1, i2) -> Double.compare(i2.score(), i1.score())) // 점수 내림차순
                .limit(10)
                .toList();

        // 2. 순위 할당 및 최종 객체 생성
        return java.util.stream.IntStream.range(0, scoredItems.size())
                .mapToObj(i -> {
                    ScoredItem item = scoredItems.get(i);
                    return new RecommendedCourse(item.courseId(), item.score(), i + 1);
                })
                .toList();
    }

    /**
     * 내부 헬퍼 레코드 (점수 계산용 임시 객체)
     */
    private record ScoredItem(CourseId courseId, double score) {}

    /**
     * Aggregate 조회 또는 생성
     */
    private MemberRecommendation findOrCreateRecommendation(MemberId memberId) {
        return recommendationRepository.findByMemberId(memberId)
                .orElseGet(() -> {
                    log.info("[신규 추천 생성] memberId={}", memberId.getValue());
                    return new MemberRecommendation(memberId);
                });
    }
}
