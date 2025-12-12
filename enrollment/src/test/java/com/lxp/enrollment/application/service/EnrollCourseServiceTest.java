package com.lxp.enrollment.application.service;
import com.lxp.api.content.course.port.external.dto.result.CourseInfoResult;
import com.lxp.common.application.port.out.DomainEventPublisher;
import com.lxp.common.enums.Level;
import com.lxp.enrollment.application.port.provided.dto.command.EnrollCourseCommand;
import com.lxp.enrollment.application.port.provided.dto.result.EnrollCourseResult;
import com.lxp.enrollment.application.port.required.UserStatusQueryPort;
import com.lxp.enrollment.domain.event.EnrollmentCreatedEvent;
import com.lxp.enrollment.domain.exception.EnrollmentException;
import com.lxp.enrollment.domain.model.Enrollment;
import com.lxp.enrollment.domain.model.enums.EnrollmentState;
import com.lxp.enrollment.domain.model.vo.CourseId;
import com.lxp.enrollment.domain.model.vo.UserId;
import com.lxp.enrollment.domain.repository.EnrollmentRepository;
import com.lxp.enrollment.infrastructure.external.adapter.CourseStatusQueryAdapter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrollCourseServiceTest {
    @Mock
    private EnrollmentRepository enrollmentRepository;
    
    @Mock
    private UserStatusQueryPort userStatusQueryPort;
    
    @Mock
    private CourseStatusQueryAdapter courseStatusQueryAdapter;

    @Mock
    private DomainEventPublisher domainEventPublisher;
    
    @InjectMocks
    private EnrollCourseService enrollCourseService;

    private EnrollCourseCommand createCommand() {
        return new EnrollCourseCommand(
                "user-uuid",
                "course-uuid"
        );
    }

    private Enrollment createEnrollmentWithEvent() {
        return Enrollment.create(
                new UserId(UUID.randomUUID().toString()),
                new CourseId(UUID.randomUUID().toString())
        );
    }

    private EnrollCourseResult createExpectedResult() {
        return new EnrollCourseResult(
                1L,
                "ENROLLED",
                LocalDateTime.now()
        );
    }
    
    private CourseInfoResult createCourseInfoResult() {
        return new CourseInfoResult(
                UUID.randomUUID().toString(),
                1L,
                "instructor-uuid",
                "테스트 강의",
                "https://example.com/thumbnail.jpg",
                "테스트 설명",
                3000L,
                Level.JUNIOR,
                List.of(),
                List.of(1L)
        );
    }

    @Test
    @DisplayName("수강 신청 시 도메인 이벤트가 발행된다")
    void enroll_shouldPublishDomainEvent() {
        // given
        EnrollCourseCommand command = createCommand();
        Enrollment enrollmentWithEvent = createEnrollmentWithEvent();
        
        when(userStatusQueryPort.isActiveUser(command.userId()))
                .thenReturn(true);
        when(courseStatusQueryAdapter.isActiveCourse(command.courseId()))
                .thenReturn(true);
        
        when(enrollmentRepository.save(any(Enrollment.class)))
                .thenReturn(Enrollment.reconstruct(
                        "enrollment-uuid",
                        1L,
                        enrollmentWithEvent.state().name(),
                        enrollmentWithEvent.userId(),
                        enrollmentWithEvent.courseId(),
                        enrollmentWithEvent.enrollmentDate().value(),
                        enrollmentWithEvent.cancelReason()
                ));

        // when
        enrollCourseService.enroll(command);
        
        // then
        verify(domainEventPublisher, times(1)).publish(any(EnrollmentCreatedEvent.class));

    }

    @Test
    @DisplayName("유효한 사용자가 아닌 경우 DomainException이 발생한다")
    void invalidUser_shouldPublishDomainEvent() {
        // given
        EnrollCourseCommand command = createCommand();
        when(userStatusQueryPort.isActiveUser(command.userId())).thenReturn(false);

        // when
        EnrollmentException exception = assertThrows(
                EnrollmentException.class,
                () -> enrollCourseService.enroll(command)
        );

        // then
        assertEquals("유효하지 않은 사용자는 수강 신청을 할 수 없습니다", exception.getMessage());
    }

    @Test
    @DisplayName("존재하지 않는 강의로 수강신청을 하면 DomainException이 발생한다")
    void invalidCourse_shouldPublishDomainEvent() {
        // given
        EnrollCourseCommand command = createCommand();
        when(userStatusQueryPort.isActiveUser(command.userId())).thenReturn(true);
        when(courseStatusQueryAdapter.isActiveCourse(command.courseId())).thenReturn(false);

        // when
        EnrollmentException exception = assertThrows(
                EnrollmentException.class,
                () -> enrollCourseService.enroll(command)
        );

        // then
        assertEquals("유효하지 않은 강의는 수강 신청을 할 수 없습니다", exception.getMessage());
    }

    @Test
    @DisplayName("유효한 사용자는 존재하는 강의를 수강할 수 있다")
    void success_enroll_withActiveUserAndActiveCourse() {
        // given
        EnrollCourseCommand command = createCommand();
        when(userStatusQueryPort.isActiveUser(command.userId())).thenReturn(true);
        when(courseStatusQueryAdapter.isActiveCourse(command.courseId())).thenReturn(true);
        when(enrollmentRepository.save(any(Enrollment.class)))
                .thenReturn(Enrollment.reconstruct(
                        "enrollment-uuid",
                        1L,
                        EnrollmentState.ENROLLED.name(),
                        command.userId(),
                        command.courseId(),
                        LocalDateTime.now(),
                        null
                ));

        // when
        EnrollCourseResult result = enrollCourseService.enroll(command);

        // then
        assertEquals(1L, result.enrollmentId());
        assertEquals(EnrollmentState.ENROLLED.name(), result.state());
    }


}