package com.lxp.recommend.domain.model;

import com.lxp.recommend.domain.model.ids.CourseId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecommendedCourse {

    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "course_id", nullable = false))
    private CourseId courseId;

    @Column(name = "score", nullable = false)
    private double score;

    @Column(name = "rank_val", nullable = false)
    private int rank;

    public RecommendedCourse(CourseId courseId, double score, int rank) {
        this.courseId = courseId;
        this.score = score;
        this.rank = rank;
    }
}
