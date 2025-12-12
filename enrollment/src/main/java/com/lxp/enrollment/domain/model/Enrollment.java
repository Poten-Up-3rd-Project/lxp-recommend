package com.lxp.enrollment.domain.model;

import com.lxp.common.domain.event.AggregateRoot;
import com.lxp.common.util.UUIdGenerator;
import com.lxp.enrollment.domain.event.CancelEnrollmentEvent;
import com.lxp.enrollment.domain.event.EnrollmentCreatedEvent;
import com.lxp.enrollment.domain.model.enums.EnrollmentState;
import com.lxp.enrollment.domain.model.vo.*;

import java.time.LocalDateTime;
import java.util.Objects;

public class Enrollment extends AggregateRoot<EnrollmentId> {
    private EnrollmentUUID enrollmentUUID;
    private EnrollmentId enrollmentId;
    private EnrollmentState state;
    private UserId userId;
    private CourseId courseId;
    private EnrollmentDate enrollmentDate;
    private String cancelReason;

    private Enrollment() {}

    private Enrollment(EnrollmentUUID enrollmentUUID, EnrollmentId enrollmentId, EnrollmentState state, UserId userId, CourseId courseId, EnrollmentDate enrollmentDate, String cancelReason) {
        this.enrollmentUUID = enrollmentUUID;
        this.enrollmentId = enrollmentId;
        this.state = state;
        this.userId = userId;
        this.courseId = courseId;
        this.enrollmentDate = enrollmentDate;
        this.cancelReason = cancelReason;
    }

    @Override
    public EnrollmentId getId() {
        return this.enrollmentId;
    }

    private Enrollment(EnrollmentUUID enrollmentUUID, EnrollmentState state, UserId userId, CourseId courseId) {
        this.enrollmentUUID = enrollmentUUID;
        this.state = state;
        this.userId = userId;
        this.courseId = courseId;
        this.enrollmentDate = new EnrollmentDate(LocalDateTime.now());
    }

    public static Enrollment reconstruct(
            String enrollmentUUID,
            long enrollmentId,
            String state,
            String userId,
            String courseId,
            LocalDateTime createdAt,
            String cancelReason
    ) {
        return new Enrollment(
                new EnrollmentUUID(enrollmentUUID),
                new EnrollmentId(enrollmentId),
                EnrollmentState.valueOf(state),
                new UserId(userId),
                new CourseId(courseId),
                new EnrollmentDate(createdAt),
                cancelReason
        );
    }

    public static Enrollment create(
            UserId userId,
            CourseId courseId
    ) {
        Objects.requireNonNull(userId, "UserId must not be null");
        Objects.requireNonNull(courseId, "CourseId must not be null");

        EnrollmentUUID enrollmentUUID = new EnrollmentUUID(UUIdGenerator.createString());
        Enrollment enrollment = new Enrollment(enrollmentUUID, EnrollmentState.ENROLLED, userId, courseId);

        enrollment.registerEvent(new EnrollmentCreatedEvent(
                enrollmentUUID.value(),
                courseId.value(),
                userId.value()
        ));

        return enrollment;
    }

    public void complete() {
        if (this.state == EnrollmentState.CANCELLED) {
            throw new IllegalStateException("취소된 수강은 완료할 수 없습니다.");
        }
        if (this.state == EnrollmentState.COMPLETED) {
            return;
        }

        this.state = EnrollmentState.COMPLETED;
    }

    public void cancel(LocalDateTime now, String cancelReason) {
        Objects.requireNonNull(now, "now must not be null");

        if (this.state == EnrollmentState.CANCELLED) {
            return;
        }

        if (this.state == EnrollmentState.COMPLETED) {
            throw new IllegalStateException("완료된 수강은 취소할 수 없습니다.");
        }

        if (!enrollmentDate.canCancel(now)) {
            throw new IllegalStateException("수강 시작 후 3일이 지나 취소할 수 없습니다.");
        }

        this.state = EnrollmentState.CANCELLED;
        this.cancelReason = cancelReason;

        registerEvent(new CancelEnrollmentEvent(enrollmentUUID.value(), userId.value(), courseId.value()));
    }

    public EnrollmentState state() {
        return this.state;
    }

    public String enrollmentUUID() {
        return this.enrollmentUUID.value();
    }

    public String userId() {
        return this.userId.value();
    }

    public String courseId() {
        return this.courseId.value();
    }

    public EnrollmentDate enrollmentDate() {
        return this.enrollmentDate;
    }

    public String cancelReason() {
        return this.cancelReason;
    }
}
