package com.lxp.enrollment.domain.model;

import com.lxp.enrollment.domain.model.enums.EnrollmentState;
import com.lxp.enrollment.domain.model.vo.CourseId;
import com.lxp.enrollment.domain.model.vo.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EnrollmentTest {
    private UserId userId() {
        return new UserId(UUID.randomUUID().toString());
    }

    private CourseId courseId() {
        return new CourseId(UUID.randomUUID().toString());
    }

    @Test
    @DisplayName("정상 생성시 초기 상태는 ENROLLED 이어야 한다")
    void create_success_initialStateEnrolled() {
        // given && when
        Enrollment enrollment = Enrollment.create(userId(), courseId());

        // then
        assertEquals(EnrollmentState.ENROLLED, enrollment.state());
    }

    @Test
    @DisplayName("UserId가 null 이면 NPE가 발생한다")
    void create_fail_whenUserIdNull() {
        // given
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> Enrollment.create(null, new CourseId(UUID.randomUUID().toString())));

        // when
        assertEquals("UserId must not be null", exception.getMessage());
    }

    @Test
    @DisplayName("CourseId가 null 이면 NPE가 발생한다")
    void create_fail_whenCourseIdNull() {
        // given
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> Enrollment.create(userId(), null)
        );

        // when
        assertEquals("CourseId must not be null", exception.getMessage());
    }

    @Test
    @DisplayName("ENROLLED 상태에서 강의 완료시 COMPLETED로 변경되어야 한다")
    void complete_success_fromEnrolledToCompleted() {
        // given
        Enrollment enrollment = Enrollment.create(
                new UserId(UUID.randomUUID().toString()),
                new CourseId(UUID.randomUUID().toString())
        );

        // when
        enrollment.complete();

        // then
        assertEquals(EnrollmentState.COMPLETED, enrollment.state());
    }

    @Test
    @DisplayName("이미 수강 완료된 상태에서 complete를 재호출해도 상태가 그대로 유지된다")
    void complete_again_whenAlreadyComplete() {
        // given
        Enrollment enrollment = Enrollment.create(
                new UserId(UUID.randomUUID().toString()),
                new CourseId(UUID.randomUUID().toString())
        );
        enrollment.complete();

        // when
        enrollment.complete();

        // then
        assertEquals(EnrollmentState.COMPLETED, enrollment.state());
    }

    @Test
    @DisplayName("CANCELED 상태에서 complete를 호출하면 예외가 발생한다")
    void complete_fail_whenCanceled() {
        // given
        Enrollment enrollment = Enrollment.create(
                new UserId(UUID.randomUUID().toString()),
                new CourseId(UUID.randomUUID().toString())
        );
        enrollment.cancel(LocalDateTime.now(), "취소 사유");

        // when
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                enrollment::complete
        );

        // then
        assertEquals("취소된 수강은 완료할 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("ENROLLED 상태에서 수강 시작 3일 이내에 취소하면 CANCELLED로 변경된다")
    void cancel_success_with3Days() {
        // given
        Enrollment enrollment = Enrollment.create(userId(), courseId());
        LocalDateTime cancelAt = enrollment.enrollmentDate().value().plusDays(3);

        // when
        enrollment.cancel(cancelAt, "취소사유");

        // then
        assertEquals(EnrollmentState.CANCELLED, enrollment.state());
    }

    @Test
    @DisplayName("cancel()에 now 파라니터가 null로 전달되면 NPE가 발생한다")
    void cancel_fail_whenNonNow() {
        // given
        Enrollment enrollment = Enrollment.create(userId(), courseId());

        // when
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> enrollment.cancel(null, "취소사유"));

        // then
        assertEquals("now must not be null", exception.getMessage());
    }

    @Test
    @DisplayName("이미 취소된 상태에서 cancel()을 호출하면 취소 상태가 그대로 유지된다")
    void cancel_again_whenAlreadyCancelled() {
        // given
        Enrollment enrollment = Enrollment.create(userId(), courseId());
        enrollment.cancel(LocalDateTime.now(), "취소사유");

        // when
        enrollment.cancel(LocalDateTime.now().plusDays(1), "취소사유");

        // then
        assertEquals(EnrollmentState.CANCELLED, enrollment.state());
    }

    @Test
    @DisplayName("COMPLETED 상태에서 cancel()을 호출하면 예외가 발생한다")
    void cancel_fail_whenCompleted() {
        // given
        Enrollment enrollment = Enrollment.create(userId(), courseId());
        enrollment.complete();

        // when
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> enrollment.cancel(LocalDateTime.now(), "취소사유"));

        // then
        assertEquals("완료된 수강은 취소할 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("수강 시작 후 3일이 지나면 취소할 수 없다")
    void cancel_fail_after3Days() {
        // given
        Enrollment enrollment = Enrollment.create(userId(), courseId());
        LocalDateTime cancelAt = enrollment.enrollmentDate().value().plusDays(4);

        // when
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> enrollment.cancel(cancelAt, "취소사유"));

        // then
        assertEquals("수강 시작 후 3일이 지나 취소할 수 없습니다.", exception.getMessage());
    }
}