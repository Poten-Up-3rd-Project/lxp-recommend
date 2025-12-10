package com.lxp.content.course.infrastructure.event.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lxp.common.event.integration.CourseCreatedIntegrationEvent;
import com.lxp.common.infrastructure.persistence.OutboxEvent;
import com.lxp.common.infrastructure.persistence.OutboxEventRepository;
import com.lxp.content.config.JpaTestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@SpringJUnitConfig(classes = JpaTestConfig.class)
@ExtendWith(MockitoExtension.class)
@DisplayName("CourseIntegrationEventPublisher 단위 테스트")
public class CourseIntegrationEventPublisherTest {

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private OutboxEventRepository outboxEventRepository;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @InjectMocks
    private CourseIntegrationEventPublisher publisher;

    @Test
    @DisplayName("IntegrationEvent 발행 시 Outbox에 저장된다")
    void publish_ShouldSaveToOutbox() {
        // Given
        CourseCreatedIntegrationEvent event = new CourseCreatedIntegrationEvent(
                "event-id",
                "course-uuid",
                "instructor-uuid",
                "테스트 강의",
                "설명",
                "https://thumbnail.url",
                "JUNIOR",
                List.of(1L, 2L),
                LocalDateTime.now()
        );

        // When
        publisher.publish(event);

        // Then
        ArgumentCaptor<OutboxEvent> captor = ArgumentCaptor.forClass(OutboxEvent.class);
        verify(outboxEventRepository).save(captor.capture());

        OutboxEvent outbox = captor.getValue();
        assertThat(outbox.getEventType()).isEqualTo("CourseCreatedIntegrationEvent");
    }

    @Test
    @DisplayName("발행 성공 시 PUBLISHED 상태가 된다")
    void publish_ShouldMarkAsPublishedOnSuccess() {
        // Given
        CourseCreatedIntegrationEvent event = createEvent();

        // When
        publisher.publish(event);

        // Then
        verify(eventPublisher).publishEvent(event);
        ArgumentCaptor<OutboxEvent> captor = ArgumentCaptor.forClass(OutboxEvent.class);
        verify(outboxEventRepository).save(captor.capture());
        OutboxEvent outbox = captor.getValue();
        // 발행 상태 확인 published 가 설정되었는지
        assertThat(outbox.getStatus()).isEqualTo(OutboxEvent.OutboxStatus.PUBLISHED);

    }

    @Test
    @DisplayName("발행 실패 시 FAILED 상태가 된다")
    void publish_ShouldMarkAsFailedOnError() {
        // Given
        CourseCreatedIntegrationEvent event = createEvent();
        doThrow(new RuntimeException("실패"))
                .when(eventPublisher).publishEvent(any());

        // When
        publisher.publish(event);

        // Then
        ArgumentCaptor<OutboxEvent> captor = ArgumentCaptor.forClass(OutboxEvent.class);
        verify(outboxEventRepository).save(captor.capture());
        OutboxEvent outbox = captor.getValue();

        assertThat(outbox.getStatus()).isEqualTo(OutboxEvent.OutboxStatus.FAILED);

    }

    private CourseCreatedIntegrationEvent createEvent() {
        return new CourseCreatedIntegrationEvent(
                "event-id", "course-uuid", "instructor-uuid",
                "테스트", "설명", "url", "JUNIOR",
                List.of(1L), LocalDateTime.now()
        );
    }


}
