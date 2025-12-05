package com.lxp.recommend.domain.service;

import com.lxp.recommend.domain.model.RecommendedCourse;
import com.lxp.recommend.domain.model.ids.CourseId;
import com.lxp.recommend.domain.dto.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationScoringService {

    // 변경된 가중치 설정
    private static final double EXPLICIT_TAG_WEIGHT = 1.0; // 사용자가 선택한 태그
    private static final double IMPLICIT_TAG_WEIGHT = 1.5; // 수강 중인 강좌의 태그

    public List<RecommendedCourse> scoreAndRank(
            LearnerProfileView learner,
            List<CourseMetaView> candidates,
            List<LearningStatusView> learningHistory
    ) {
        // 1. 수강 중이거나 완료한 강좌 ID 추출 (제외용 + 태그 수집용)
        Set<Long> enrolledCourseIds = new HashSet<>();
        Set<Long> completedCourseIds = new HashSet<>();

        for (LearningStatusView history : learningHistory) {
            if (history.status() == EnrollmentStatus.ENROLLED) {
                enrolledCourseIds.add(history.courseId());
            } else if (history.status() == EnrollmentStatus.COMPLETED) {
                completedCourseIds.add(history.courseId());
            }
        }

        // 2. 수강 중인 강좌들의 태그 수집 (Implicit Tags)
        // 주의: candidates 목록에 없는 강좌(다른 난이도 등)의 태그는 알 수 없다는 한계가 있음.
        // 완벽하게 하려면 별도로 태그를 조회해야 하지만, MVP에서는 candidates 내에서만 찾아도 무방.
        Set<String> implicitTags = candidates.stream()
                .filter(c -> enrolledCourseIds.contains(c.courseId()))
                .flatMap(c -> c.tags().stream())
                .collect(Collectors.toSet());

        // 3. 점수 계산 및 정렬
        List<ScoredCourse> scoredList = candidates.stream()
                // 이미 수강/완료한 강좌는 추천 리스트에서 제외
                .filter(c -> !enrolledCourseIds.contains(c.courseId()) && !completedCourseIds.contains(c.courseId()))
                .map(c -> scoreCourse(c, learner.selectedTags(), implicitTags))
                .filter(sc -> sc.score > 0) // 관련 없는 강좌 제외
                .sorted(Comparator.comparingDouble(ScoredCourse::score).reversed()) // 점수 내림차순
                .toList();

        // 4. Top 10 추출 (무한 스크롤 고려하여 10개까지)
        List<RecommendedCourse> result = new ArrayList<>();
        int limit = Math.min(scoredList.size(), 10); // 기획 변경: 최대 10개

        for (int i = 0; i < limit; i++) {
            ScoredCourse sc = scoredList.get(i);
            result.add(new RecommendedCourse(
                    CourseId.of(sc.courseId()),
                    sc.score(),
                    i + 1
            ));
        }

        return result;
    }

    // 개별 강좌 점수 계산 로직 (통합 태그 + 가중치 적용)
    private ScoredCourse scoreCourse(CourseMetaView course, Set<String> explicitTags, Set<String> implicitTags) {
        double totalScore = 0.0;

        if (course.tags() != null) {
            for (String tag : course.tags()) {
                boolean isImplicit = implicitTags != null && implicitTags.contains(tag);
                boolean isExplicit = explicitTags != null && explicitTags.contains(tag);

                if (isImplicit) {
                    totalScore += IMPLICIT_TAG_WEIGHT; // 1.5점
                } else if (isExplicit) {
                    totalScore += EXPLICIT_TAG_WEIGHT; // 1.0점
                }
            }
        }

        // (옵션) 보조 정렬 기준: 최신성이나 인기 점수가 있다면 여기서 가산점 부여 가능

        return new ScoredCourse(course.courseId(), totalScore);
    }

    private record ScoredCourse(Long courseId, double score) {}
}
