package com.lxp.recommend.domain.model;

import com.lxp.common.domain.event.AggregateRoot; // common-lib import
// import com.lxp.recommend.domain.event.RecommendationUpdatedEvent; // (이벤트 클래스 생성 시 주석 해제)
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
// AggregateRoot 상속: 도메인 이벤트 기능(registerEvent) 사용 가능
public class MemberRecommendation extends AggregateRoot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
        // 생성 시 이벤트가 필요하다면 여기서도 발행 가능
    }

    public void updateItems(List<RecommendedCourse> newItems) {
        this.items.clear();
        this.items.addAll(newItems);
        this.calculatedAt = LocalDateTime.now();

        // 도메인 이벤트 발행 예시 (선택 사항)
        // 만약 '추천 갱신 알림' 기능이 필요하다면 아래 주석을 해제하고 이벤트 클래스를 만드세요.
        /*
        this.registerEvent(new RecommendationUpdatedEvent(
            String.valueOf(this.id), // aggregateId
            this.memberId.getValue() // memberId
        ));
        */
    }

    // BaseEntity를 상속받지 못했으므로, id 기반 equals/hashCode 직접 구현 (필요 시)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MemberRecommendation that)) return false;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
