package com.lxp.recommend.domain.result.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "recommend_result", indexes = {
        @Index(name = "idx_recommend_result_user_id", columnList = "user_id", unique = true)
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RecommendResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", length = 36, nullable = false, unique = true)
    private String userId;

    @Type(JsonType.class)
    @Column(name = "course_ids", columnDefinition = "json", nullable = false)
    @Builder.Default
    private List<String> courseIds = new ArrayList<>();

    @Column(name = "batch_id", length = 50, nullable = false)
    private String batchId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void updateRecommendations(List<String> courseIds, String batchId) {
        this.courseIds = new ArrayList<>(courseIds);
        this.batchId = batchId;
    }
}
