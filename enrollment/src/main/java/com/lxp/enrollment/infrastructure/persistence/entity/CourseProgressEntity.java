package com.lxp.enrollment.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "course_progress", uniqueConstraints = {
    @UniqueConstraint(
        name = "UC_USER_COURSE_PROGRESS",
        columnNames = {"user_id", "course_id"}
    )
})
public class CourseProgressEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_progress_id")
    private Long id;

    @Column(name = "user_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID userId;

    @Column(name = "course_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID courseId;

    @Column(name = "total_progress", nullable = false)
    private Float totalProgress;

    @Column(name = "progress_status", nullable = false)
    private String status;

    private OffsetDateTime completedAt;

    @OneToMany(mappedBy = "courseProgress", fetch = FetchType.LAZY)
    List<LectureProgressEntity> lectureProgressList;

    protected CourseProgressEntity() {}

}
