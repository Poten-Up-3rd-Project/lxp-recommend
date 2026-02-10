package com.lxp.recommend.domain.course.entity;

import com.lxp.recommend.domain.user.entity.Level;
import com.lxp.recommend.domain.user.entity.Status;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "recommend_course", indexes = {
        @Index(name = "idx_recommend_course_status", columnList = "status"),
        @Index(name = "idx_recommend_course_instructor", columnList = "instructor_id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RecommendCourse {

    @Id
    @Column(length = 36)
    private String id;

    @Type(JsonType.class)
    @Column(name = "tags", columnDefinition = "json", nullable = false)
    @Builder.Default
    private List<Long> tags = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private Level level;

    @Column(name = "instructor_id", length = 36, nullable = false)
    private String instructorId;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    @Builder.Default
    private Status status = Status.ACTIVE;

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

    public void deactivate() {
        this.status = Status.INACTIVE;
    }

    public void activate() {
        this.status = Status.ACTIVE;
    }
}
