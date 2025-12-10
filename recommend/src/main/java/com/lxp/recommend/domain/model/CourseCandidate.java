package com.lxp.recommend.domain.model;

import com.lxp.recommend.domain.dto.DifficultyLevel;
import com.lxp.recommend.domain.model.ids.CourseId;

import java.util.Set;

/**
 * 추천 후보 강좌의 메타 정보
 *
 * 책임:
 * - 강좌의 추천 관련 속성만 포함 (태그, 난이도)
 * - 도메인 규칙 (난이도 매칭 등)
 */
public record CourseCandidate(
        CourseId courseId,
        Set<String> tags,
        DifficultyLevel difficulty,
        boolean isPublic
) {
    /**
     * Compact Constructor: 불변성 보장 및 검증
     */
    public CourseCandidate {
        if (courseId == null) {
            throw new IllegalArgumentException("courseId는 null일 수 없습니다.");
        }
        tags = tags != null ? Set.copyOf(tags) : Set.of();
        if (difficulty == null) {
            throw new IllegalArgumentException("difficulty는 null일 수 없습니다.");
        }
    }

    /**
     * 특정 난이도 목록에 매칭되는지 확인
     */
    public boolean matchesDifficulty(Set<DifficultyLevel> targetLevels) {
        return targetLevels.contains(difficulty);
    }

    /**
     * 태그가 하나라도 있는지 확인
     */
    public boolean hasTags() {
        return !tags.isEmpty();
    }

    /**
     * 특정 태그를 포함하는지 확인
     */
    public boolean hasTag(String tag) {
        return tags.contains(tag);
    }

    @Override
    public String toString() {
        return String.format("CourseCandidate(id=%s, difficulty=%s, tags=%d)",
                courseId.getValue(), difficulty, tags.size());
    }
}
