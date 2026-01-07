package com.lxp.recommend.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "member_recommendations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberRecommendationJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false, unique = true)
    private String memberId; // VO 대신 원시 타입

    @OneToMany(
            mappedBy = "recommendation",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    @OrderColumn(name = "item_index")
    private List<RecommendedCourseItemJpaEntity> items = new ArrayList<>();

    @Column(name = "calculated_at", nullable = false)
    private LocalDateTime calculatedAt;

    // 생성 메서드 (Mapper에서 호출)
    public static MemberRecommendationJpaEntity of(
            String memberId,
            List<RecommendedCourseItemJpaEntity> items,
            LocalDateTime calculatedAt
    ) {
        MemberRecommendationJpaEntity entity = new MemberRecommendationJpaEntity();
        entity.memberId = memberId;
        entity.calculatedAt = calculatedAt;
        entity.items.addAll(items);

        // 양방향 연관관계 설정
        items.forEach(item -> item.setRecommendation(entity));

        return entity;
    }

    // 업데이트 메서드
    public void updateItems(List<RecommendedCourseItemJpaEntity> newItems) {
        this.items.clear();
        this.items.addAll(newItems);
        newItems.forEach(item -> item.setRecommendation(this));
        this.calculatedAt = LocalDateTime.now();
    }
}
