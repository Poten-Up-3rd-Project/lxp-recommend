package com.lxp.recommend.domain.model;

import com.lxp.recommend.domain.model.ids.MemberId;
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
public class MemberRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto Increment
    private Long id;

    // VO를 Embedded로 사용.
    // @AttributeOverride를 써서 컬럼명 지정 가능 (기본은 필드명인 id가 되므로 수정 필요)
    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "member_id", nullable = false, unique = true))
    private MemberId memberId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "recommended_course_items",
            joinColumns = @JoinColumn(name = "recommendation_id")
    )
    @OrderColumn(name = "item_index")
    private List<RecommendedCourse> items = new ArrayList<>();

    @Column(name = "calculated_at", nullable = false)
    private LocalDateTime calculatedAt;

    public MemberRecommendation(MemberId memberId) {
        this.memberId = memberId;
        this.calculatedAt = LocalDateTime.now();
    }

    public void updateItems(List<RecommendedCourse> newItems) {
        this.items.clear();
        this.items.addAll(newItems);
        this.calculatedAt = LocalDateTime.now();
    }
}
