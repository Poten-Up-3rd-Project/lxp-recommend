package com.lxp.recommend.domain.service;

import com.lxp.recommend.domain.model.RecommendedCourse;
import com.lxp.recommend.domain.model.ids.CourseId; // VO
import com.lxp.recommend.domain.dto.*;
import org.springframework.stereotype.Service; // Domain Service도 빈 등록 가능

import java.util.*;
import java.util.stream.Collectors;

@Service // Spring Bean으로 등록 (순수 자바 객체로 써도 되지만, 주입받아 쓰기 위해)
public class RecommendationScoringService {

    // 가중치 설정 (상수 또는 설정 파일 주입 가능)
    private static final double INTEREST_TAG_WEIGHT = 5.0;
    private static final double SKILL_MATCH_WEIGHT = 3.0;
    private static final double LEVEL_MATCH_WEIGHT = 1.0;

    public List<RecommendedCourse> scoreAndRank(
            LearnerProfileView learner,
            List<CourseMetaView> candidates, // 1차 필터링된 후보군
            List<LearningStatusView> learningStatuses
    ) {
        // 1. 이미 수강 중이거나 완료한 강좌 ID 추출 (제외용)
        Set<Long> excludedCourseIds = learningStatuses.stream()
                .filter(ls -> ls.status() != EnrollmentStatus.CANCELLED)
                .map(LearningStatusView::courseId)
                .collect(Collectors.toSet());

        // 2. 점수 계산 및 정렬
        List<ScoredCourse> scoredList = candidates.stream()
                // 수강 이력이 있는 강좌 제외
                .filter(c -> !excludedCourseIds.contains(c.courseId()))
                // 점수 계산
                .map(c -> scoreCourse(learner, c))
                // 점수가 0점 초과인 것만 남김
                .filter(sc -> sc.score() > 0)
                // 점수 내림차순 정렬
                .sorted(Comparator.comparingDouble(ScoredCourse::score).reversed())
                .toList();

        // 3. Top 4 추출 및 도메인 객체로 변환
        List<RecommendedCourse> result = new ArrayList<>();
        int limit = Math.min(scoredList.size(), 4);

        for (int i = 0; i < limit; i++) {
            ScoredCourse sc = scoredList.get(i);
            // CourseId VO 생성 및 RecommendedCourse 생성
            result.add(new RecommendedCourse(
                    CourseId.of(sc.courseId()),
                    sc.score(),
                    i + 1 // rank (1부터 시작)
            ));
        }

        return result;
    }

    // 개별 강좌 점수 계산 로직
    private ScoredCourse scoreCourse(LearnerProfileView learner, CourseMetaView course) {

        // 태그 교집합 개수
        long tagMatchCount = intersectionCount(learner.interestTags(), course.tags());

        // 스킬 교집합 개수
        long skillMatchCount = intersectionCount(learner.skills(), course.requiredSkills());

        // 레벨 점수 (같으면 3, 인접하면 1, 멀면 0)
        double levelScore = calculateLevelScore(learner.level(), course.difficulty());

        double totalScore = (tagMatchCount * INTEREST_TAG_WEIGHT)
                + (skillMatchCount * SKILL_MATCH_WEIGHT)
                + (levelScore * LEVEL_MATCH_WEIGHT);

        return new ScoredCourse(course.courseId(), totalScore);
    }

    private long intersectionCount(Set<String> setA, Set<String> setB) {
        if (setA == null || setB == null) return 0;
        Set<String> intersection = new HashSet<>(setA);
        intersection.retainAll(setB);
        return intersection.size();
    }

    private double calculateLevelScore(DifficultyLevel userLevel, DifficultyLevel courseLevel) {
        if (userLevel == null || courseLevel == null) return 0;

        int diff = Math.abs(userLevel.ordinal() - courseLevel.ordinal());
        if (diff == 0) return 3.0; // 동일 레벨
        if (diff == 1) return 1.0; // 인접 레벨
        return 0.0;
    }

    // 내부 계산용 임시 레코드
    private record ScoredCourse(Long courseId, double score) {}
}
