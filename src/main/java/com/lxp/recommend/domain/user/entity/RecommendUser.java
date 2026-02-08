package com.lxp.recommend.domain.user.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "recommend_user", indexes = {
        @Index(name = "idx_recommend_user_status", columnList = "status")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RecommendUser {

    @Id
    @Column(length = 36)
    private String id;

    @Type(JsonType.class)
    @Column(name = "interest_tags", columnDefinition = "json", nullable = false)
    @Builder.Default
    private List<Long> interestTags = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private Level level;

    @Type(JsonType.class)
    @Column(name = "enrolled_course_ids", columnDefinition = "json", nullable = false)
    @Builder.Default
    private List<String> enrolledCourseIds = new ArrayList<>();

    @Type(JsonType.class)
    @Column(name = "created_course_ids", columnDefinition = "json", nullable = false)
    @Builder.Default
    private List<String> createdCourseIds = new ArrayList<>();

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

    public void updateProfile(List<Long> interestTags, Level level) {
        this.interestTags = new ArrayList<>(interestTags);
        this.level = level;
    }

    public void deactivate() {
        this.status = Status.INACTIVE;
    }

    public void activate() {
        this.status = Status.ACTIVE;
    }

    public void addEnrolledCourse(String courseId) {
        if (!enrolledCourseIds.contains(courseId)) {
            this.enrolledCourseIds = new ArrayList<>(enrolledCourseIds);
            this.enrolledCourseIds.add(courseId);
        }
    }

    public void removeEnrolledCourse(String courseId) {
        this.enrolledCourseIds = new ArrayList<>(enrolledCourseIds);
        this.enrolledCourseIds.remove(courseId);
    }

    public void addCreatedCourse(String courseId) {
        if (!createdCourseIds.contains(courseId)) {
            this.createdCourseIds = new ArrayList<>(createdCourseIds);
            this.createdCourseIds.add(courseId);
        }
    }

    public void removeCreatedCourse(String courseId) {
        this.createdCourseIds = new ArrayList<>(createdCourseIds);
        this.createdCourseIds.remove(courseId);
    }
}
