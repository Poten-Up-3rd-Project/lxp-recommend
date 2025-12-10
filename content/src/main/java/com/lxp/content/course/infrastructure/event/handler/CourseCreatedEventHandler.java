package com.lxp.content.course.infrastructure.event.handler;

import com.lxp.common.event.integration.CourseCreatedIntegrationEvent;
import com.lxp.common.util.UUIdGenerator;
import com.lxp.content.course.domain.event.CourseCreatedEvent;
import com.lxp.content.course.infrastructure.event.integration.CourseIntegrationEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CourseCreatedEventHandler {
    private final CourseIntegrationEventPublisher integrationEventPublisher;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(CourseCreatedEvent event) {
        CourseCreatedIntegrationEvent integrationEvent = new CourseCreatedIntegrationEvent(
                UUIdGenerator.createString(),
                event.getAggregateId(),
                event.getInstructorUuid(),
                event.getTitle(),
                event.getDescription(),
                event.getThumbnailUrl(),
                event.getDifficulty().name(),
                event.getTagIds(),
                LocalDateTime.now()
        );


        integrationEventPublisher.publish(integrationEvent);
    }
}
