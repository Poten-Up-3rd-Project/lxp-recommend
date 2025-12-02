package com.lxp.recommend.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecommendedCourse {

    @Column(name = "course_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID courseId;

    @Column(name = "score", nullable = false)
    private double score;

    @Column(name = "rank_val", nullable = false) // 'rank'는 예약어일 가능성 있음
    private int rank;

    // 생성자
    public RecommendedCourse(UUID courseId, double score, int rank) {
        this.courseId = courseId;
        this.score = score;
        this.rank = rank;
    }
}
