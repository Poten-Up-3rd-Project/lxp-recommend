package com.lxp.recommend.domain.model;

import com.lxp.recommend.domain.model.ids.EnrollmentStatus;
import com.lxp.recommend.domain.exception.InvalidRecommendContextException;
import com.lxp.recommend.domain.model.ids.CourseId;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 추천 계산에 필요한 모든 컨텍스트를 조립하고 검증하는 도메인 객체
 *
 * 책임:
 * 1. Raw Data를 받아 도메인 규칙에 따라 조립
 * 2. 제외할 강좌 계산 (수강 중/완료)
 * 3. Implicit 태그 추출 (수강 중인 강좌의 태그)
 * 4. 필터링된 후보 강좌 생성
 * 5. 불변식 검증
 */

public class RecommendContext {

    private final Set<String> explicitTags;
    private final List<LearningHistory> learningHistories;
    private final List<CourseCandidate> allCandidates;

    // 조립된 결과 (생성 시점에 계산)
    private final TagContext tagContext;
    private final Set<CourseId> excludedCourseIds;
    private final List<CourseCandidate> filteredCandidates;

    private RecommendContext(
            Set<String> explicitTags,
            List<LearningHistory> learningHistories, // ✅ 변경
            List<CourseCandidate> allCandidates
    ) {
        // null 방어
        this.explicitTags = explicitTags != null ? Set.copyOf(explicitTags) : Set.of();
        this.learningHistories = learningHistories != null ? List.copyOf(learningHistories) : List.of();
        this.allCandidates = allCandidates != null ? List.copyOf(allCandidates) : List.of();

        // 도메인 규칙에 따른 조립
        this.excludedCourseIds = buildExcludedCourseIds();
        this.tagContext = buildTagContext();
        this.filteredCandidates = buildFilteredCandidates();

        // 불변식 검증
        validateInvariants();
    }

    /**
     * RecommendContext 생성
     *
     * @param explicitTags 사용자가 선택한 관심 태그
     * @param learningHistories 학습 이력 (수강 중/완료 강좌)
     * @param allCandidates 추천 후보 강좌 (난이도 필터링 완료)
     * @return 조립된 RecommendContext
     */
    public static RecommendContext create(
            Set<String> explicitTags,
            List<LearningHistory> learningHistories,
            List<CourseCandidate> allCandidates
    ) {
        return new RecommendContext(explicitTags, learningHistories, allCandidates);
    }

    // ===== 도메인 규칙 1: 제외할 강좌 계산 =====

    /**
     * 수강 중(ENROLLED) 또는 완료(COMPLETED) 강좌는 추천에서 제외
     */
    private Set<CourseId> buildExcludedCourseIds() {
        return learningHistories.stream()
                .filter(this::shouldExclude)
                .map(LearningHistory::courseId) // ✅ 변경
                .collect(Collectors.toSet());
    }

    private boolean shouldExclude(LearningHistory history) { // ✅ 변경
        EnrollmentStatus status = history.status();
        return status == EnrollmentStatus.ENROLLED || status == EnrollmentStatus.COMPLETED;
    }

    // ===== 도메인 규칙 2: Implicit 태그 추출 =====
    /**
     * Implicit 태그 = 수강 중인 강좌의 태그
     * (완료 강좌는 포함하지 않음)
     */
    private TagContext buildTagContext() {
        Set<CourseId> enrolledCourseIds = learningHistories.stream()
                .filter(history -> history.status() == EnrollmentStatus.ENROLLED)
                .map(LearningHistory::courseId)
                .collect(Collectors.toSet());

        // 핵심 비즈니스 규칙: 수강 중인 강좌의 태그가 Implicit 태그
        Set<String> implicitTags = allCandidates.stream()
                .filter(candidate -> enrolledCourseIds.contains(candidate.getCourseId()))
                .flatMap(candidate -> candidate.getTags().stream())
                .collect(Collectors.toSet());

        return new TagContext(explicitTags, implicitTags);
    }

    // ===== 도메인 규칙 3: 추천 후보 필터링 =====
    /**
     * 제외 대상 강좌를 걸러낸 최종 후보 리스트
     */
    private List<CourseCandidate> buildFilteredCandidates() {
        return allCandidates.stream()
                .filter(candidate -> !excludedCourseIds.contains(candidate.getCourseId()))
                .toList();
    }

    // ===== 불변식 검증 =====
    private void validateInvariants() {
        // 불변식 1: 필터링된 후보에 제외 강좌가 없어야 함
        boolean hasExcludedCourse = filteredCandidates.stream()
                .anyMatch(candidate -> excludedCourseIds.contains(candidate.getCourseId()));

        if (hasExcludedCourse) {
            throw new InvalidRecommendContextException(
                    "필터링된 후보에 제외되어야 할 강좌가 포함되어 있습니다."
            );
        }

        // 불변식 2: TagContext가 null이 아니어야 함
        if (tagContext == null) {
            throw new InvalidRecommendContextException("TagContext가 null입니다.");
        }
    }

    // ===== 상태 조회 =====

    /**
     * 추천을 생성할 수 있는 유효한 컨텍스트인지 확인
     *
     * @return 태그가 하나라도 있고, 후보 강좌가 있으면 true
     */
    public boolean hasValidContext() {
        return tagContext.hasAnyTag() && !filteredCandidates.isEmpty();
    }

    public boolean isEmpty() {
        return filteredCandidates.isEmpty();
    }

    // ===== Getters =====

    public TagContext getTagContext() {
        return tagContext;
    }

    public List<CourseCandidate> getFilteredCandidates() {
        return List.copyOf(filteredCandidates); // 불변 리스트 반환
    }

    public Set<CourseId> getExcludedCourseIds() {
        return Set.copyOf(excludedCourseIds);
    }

    public int candidateCount() {
        return filteredCandidates.size();
    }
}
