package com.lxp.enrollment.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "lecture_progress", uniqueConstraints = {
        @UniqueConstraint(
                name = "UC_USER_LECTURE_PROGRESS",
                columnNames = {"user_id", "lecture_id"}
        )
})
public class LectureProgressEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lecture_progress_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "lecture_id", nullable = false)
    private UUID lectureId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "course_progress_id")
    private CourseProgressEntity courseProgress;

    protected LectureProgressEntity() {}

}
