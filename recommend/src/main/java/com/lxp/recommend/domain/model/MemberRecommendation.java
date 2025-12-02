package com.lxp.recommend.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "member_recommendations",
        indexes = @Index(name = "idx_member_recommendation_member_id", columnList = "member_id"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA용 기본 생성자
public class MemberRecommendation {

    @Id
    @UuidGenerator
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "member_id", nullable = false, unique = true, columnDefinition = "BINARY(16)")
    private UUID memberId;

    // 값 객체(VO) 컬렉션 매핑 (RecommendedCourse)
    // 생명주기를 Aggregate Root가 관리하므로 CascadeType.ALL, OrphanRemoval = true
    @ElementCollection(fetch = FetchType.EAGER) // Top 4개뿐이라 EAGER도 무방
    @CollectionTable(
            name = "recommended_course_items",
            joinColumns = @JoinColumn(name = "recommendation_id")
    )
    @OrderColumn(name = "item_index") // 리스트 순서 보장 (Rank 순)
    private List<RecommendedCourse> items = new ArrayList<>();

    @Column(name = "calculated_at", nullable = false)
    private LocalDateTime calculatedAt;

    // 생성자: 식별자와 회원 ID로 생성
    public MemberRecommendation(UUID memberId) {
        this.memberId = memberId;
        this.calculatedAt = LocalDateTime.now();
    }

    /**
     * 도메인 로직: 추천 목록 갱신
     * - 기존 아이템을 모두 지우고 새로운 아이템으로 교체 (Replace)
     * - 계산 시간 갱신
     */
    public void updateItems(List<RecommendedCourse> newItems) {
        this.items.clear();
        this.items.addAll(newItems);
        this.calculatedAt = LocalDateTime.now();
    }
}