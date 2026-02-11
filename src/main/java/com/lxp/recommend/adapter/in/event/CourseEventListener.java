package com.lxp.recommend.adapter.in.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lxp.recommend.adapter.in.event.dto.IntegrationEvent;
import com.lxp.recommend.adapter.in.event.dto.payload.CourseCreatedPayload;
import com.lxp.recommend.adapter.in.event.dto.payload.CourseDeletedPayload;
import com.lxp.recommend.application.dto.CourseSyncCommand;
import com.lxp.recommend.application.port.in.CourseSyncUseCase;
import com.lxp.recommend.global.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CourseEventListener {

    private static final String COURSE_CREATED = "course.created";
    private static final String COURSE_DELETED = "course.deleted";

    private final CourseSyncUseCase courseSyncUseCase;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = RabbitMQConfig.COURSE_QUEUE)
    public void handle(String message) {
        try {
            IntegrationEvent event = objectMapper.readValue(message, IntegrationEvent.class);
            processEvent(event);
        } catch (Exception e) {
            log.error("Failed to process: {}", message, e);
            throw new RuntimeException(e);
        }
    }

    private void processEvent(IntegrationEvent event) throws Exception {
        switch (event.eventType()) {
            case COURSE_CREATED -> handleCreated(event);
            case COURSE_DELETED -> handleDeleted(event);
            default -> log.warn("Unknown event type: {}", event.eventType());
        }
    }

    private void handleCreated(IntegrationEvent event) throws Exception {
        CourseCreatedPayload payload = parsePayload(event, CourseCreatedPayload.class);
        courseSyncUseCase.createCourse(toCommand(event.eventId(), payload));
    }

    private void handleDeleted(IntegrationEvent event) throws Exception {
        CourseDeletedPayload payload = parsePayload(event, CourseDeletedPayload.class);
        courseSyncUseCase.deleteCourse(event.eventId(), payload.courseUuid());
    }

    private <T> T parsePayload(IntegrationEvent event, Class<T> payloadClass) throws Exception {
        return objectMapper.treeToValue(event.payload(), payloadClass);
    }

    private CourseSyncCommand toCommand(String eventId, CourseCreatedPayload payload) {
        return new CourseSyncCommand(
                eventId,
                payload.courseUuid(),
                payload.tagIds(),
                payload.difficulty(),
                payload.instructorUuid()
        );
    }
}