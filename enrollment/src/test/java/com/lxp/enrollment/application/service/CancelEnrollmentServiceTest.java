package com.lxp.enrollment.application.service;

import com.lxp.common.application.port.out.DomainEventPublisher;
import com.lxp.enrollment.application.port.provided.dto.command.CancelEnrollmentCommand;
import com.lxp.enrollment.application.port.provided.dto.result.CancelEnrollmentResult;
import com.lxp.enrollment.domain.event.CancelEnrollmentEvent;
import com.lxp.enrollment.domain.model.Enrollment;
import com.lxp.enrollment.domain.repository.EnrollmentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CancelEnrollmentServiceTest {
    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private DomainEventPublisher domainEventPublisher;

    @InjectMocks
    private CancelEnrollmentService cancelEnrollmentService;

    private CancelEnrollmentResult cancelEnrollmentResult() {
        return new CancelEnrollmentResult(
                1L,
                "CANCELLED",
                LocalDateTime.now()
        );
    }

    private CancelEnrollmentCommand cancelEnrollmentCommand() {
        return new CancelEnrollmentCommand(
               1L,
               "환불 요청"
        );
    }

    @Test
    @DisplayName("수강 취소에 성공하면 도메인 이벤트가 발행된다")
    void cnacelEnrollment_shouldPublishDomainEvent() {
        // given
        CancelEnrollmentResult cancelEnrollmentResult = cancelEnrollmentResult();
        CancelEnrollmentCommand cancelEnrollmentCommand = cancelEnrollmentCommand();
        when(enrollmentRepository.findById(1L))
                .thenReturn(Enrollment.reconstruct("enrollment-uuid", 1L, "ENROLLED", "user-uuid", "course-uuid", LocalDateTime.now(), null));

        // when
        cancelEnrollmentService.cancel(cancelEnrollmentCommand);

        // then
        verify(domainEventPublisher, times(1)).publish(any(CancelEnrollmentEvent.class));
    }
}