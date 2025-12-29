package com.lxp.recommend.domain.model;

import com.lxp.common.enums.Level;  // ← 변경
import com.lxp.recommend.domain.model.ids.CourseId;

import java.util.Set;

public class CourseCandidate {
    private final CourseId courseId;
    private final Set<String> tags;
    private final Level difficulty;  // ← DifficultyLevel → Level
    private final boolean isPublic;

    public CourseCandidate(CourseId courseId, Set<String> tags, Level difficulty, boolean isPublic) {
        this.courseId = courseId;
        this.tags = tags != null ? Set.copyOf(tags) : Set.of();
        this.difficulty = difficulty;
        this.isPublic = isPublic;
    }

    // Getters
    public CourseId getCourseId() { return courseId; }
    public Set<String> getTags() { return Set.copyOf(tags); }
    public Level getDifficulty() { return difficulty; }  // ← 반환 타입 변경
    public boolean isPublic() { return isPublic; }
}
