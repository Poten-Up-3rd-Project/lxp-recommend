package com.lxp.content.course.infrastructure.event.handler;

import com.lxp.common.event.integration.CourseCreatedIntegrationEvent;
import com.lxp.content.course.domain.event.CourseCreatedEvent;
import com.lxp.content.course.domain.model.enums.CourseDifficulty;
import com.lxp.content.course.infrastructure.event.integration.CourseIntegrationEventPublisher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("CourseCreatedEventHandler 단위 테스트")
public class CourseCreatedEventHandlerTest {

    @Mock
    private CourseIntegrationEventPublisher integrationEventPublisher;

    @InjectMocks
    private CourseCreatedEventHandler handler;


    @Test
    @DisplayName("DomainEvent를 IntegrationEvent로 변환하여 발행한다")
    void handle_ShouldConvertAndPublishIntegrationEvent() {
        // Given
        CourseCreatedEvent domainEvent = new CourseCreatedEvent(
                "course-uuid",
                "instructor-uuid",
                "테스트 강의",
                "설명",
                "https://thumbnail.url",
                CourseDifficulty.JUNIOR,
                List.of(1L, 2L)
        );

        // When
        handler.handle(domainEvent);

        // Then
        ArgumentCaptor<CourseCreatedIntegrationEvent> captor =
                ArgumentCaptor.forClass(CourseCreatedIntegrationEvent.class);
        verify(integrationEventPublisher).publish(captor.capture());

        CourseCreatedIntegrationEvent integrationEvent = captor.getValue();
        assertThat(integrationEvent.courseUuid()).isEqualTo("course-uuid");
        assertThat(integrationEvent.title()).isEqualTo("테스트 강의");
        assertThat(integrationEvent.tagIds()).containsExactly(1L, 2L);
    }
}
