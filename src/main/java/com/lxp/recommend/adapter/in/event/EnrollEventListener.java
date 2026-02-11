package com.lxp.recommend.adapter.in.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lxp.recommend.adapter.in.event.dto.IntegrationEvent;
import com.lxp.recommend.adapter.in.event.dto.payload.EnrollEventPayload;
import com.lxp.recommend.application.dto.EnrollSyncCommand;
import com.lxp.recommend.application.port.in.EnrollSyncUseCase;
import com.lxp.recommend.global.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EnrollEventListener {

    private static final String ENROLL_CREATED = "enroll.created";
    private static final String ENROLL_DELETED = "enroll.deleted";

    private final EnrollSyncUseCase enrollSyncUseCase;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = RabbitMQConfig.ENROLL_QUEUE)
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
            case ENROLL_CREATED -> handleCreated(event);
            case ENROLL_DELETED -> handleDeleted(event);
            default -> log.warn("Unknown event type: {}", event.eventType());
        }
    }

    private void handleCreated(IntegrationEvent event) throws Exception {
        EnrollEventPayload payload = parsePayload(event);
        enrollSyncUseCase.createEnrollment(toCommand(event.eventId(), payload));
    }

    private void handleDeleted(IntegrationEvent event) throws Exception {
        EnrollEventPayload payload = parsePayload(event);
        enrollSyncUseCase.deleteEnrollment(toCommand(event.eventId(), payload));
    }

    private EnrollEventPayload parsePayload(IntegrationEvent event) throws Exception {
        return objectMapper.treeToValue(event.payload(), EnrollEventPayload.class);
    }

    private EnrollSyncCommand toCommand(String eventId, EnrollEventPayload payload) {
        return new EnrollSyncCommand(
                eventId,
                payload.userId(),
                payload.courseId()
        );
    }
}