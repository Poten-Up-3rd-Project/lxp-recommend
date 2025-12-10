package com.lxp.recommend.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "recommended_course_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecommendedCourseItemJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recommendation_id", nullable = false)
    private MemberRecommendationJpaEntity recommendation;

    @Column(name = "course_id", nullable = false)
    private String courseId;

    @Column(name = "score", nullable = false)
    private double score;

    @Column(name = "rank_val", nullable = false)
    private int rank;

    public static RecommendedCourseItemJpaEntity of(
            String courseId,
            double score,
            int rank
    ) {
        RecommendedCourseItemJpaEntity item = new RecommendedCourseItemJpaEntity();
        item.courseId = courseId;
        item.score = score;
        item.rank = rank;
        return item;
    }

    // 양방향 연관관계 설정용
    void setRecommendation(MemberRecommendationJpaEntity recommendation) {
        this.recommendation = recommendation;
    }
}
