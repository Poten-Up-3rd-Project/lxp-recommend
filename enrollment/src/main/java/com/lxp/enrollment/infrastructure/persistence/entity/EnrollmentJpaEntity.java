package com.lxp.enrollment.infrastructure.persistence.entity;

import com.lxp.common.infrastructure.persistence.BaseVersionedJpaEntity;
import com.lxp.enrollment.domain.model.Enrollment;
import com.lxp.enrollment.infrastructure.persistence.enums.EnrollmentState;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "enrollment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EnrollmentJpaEntity extends BaseVersionedJpaEntity {
    @Column(nullable = false, unique = true)
    private String uuid;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EnrollmentState state;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String courseId;

    @Column(name = "reason")
    private String cancelReason;

    private EnrollmentJpaEntity(String uuid, EnrollmentState state, String userId, String courseId, String cancelReason) {
        this.uuid = uuid;
        this.state = state;
        this.userId = userId;
        this.courseId = courseId;
        this.cancelReason = cancelReason;
    }

    public static EnrollmentJpaEntity from(Enrollment enrollment) {
        return new EnrollmentJpaEntity(
                enrollment.enrollmentUUID(),
                EnrollmentState.from(enrollment.state().toString()),
                enrollment.userId(),
                enrollment.courseId(),
                enrollment.cancelReason()
        );
    }

    public Enrollment toDomain() {
        return Enrollment.reconstruct(this.uuid, this.getId(), this.state.toString(), this.userId, this.courseId, this.getCreatedAt(), this.cancelReason);
    }
}