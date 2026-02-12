package com.lxp.recommend.adapter.in.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lxp.recommend.adapter.in.event.dto.IntegrationEvent;
import com.lxp.recommend.adapter.in.event.dto.payload.UserCreatedPayload;
import com.lxp.recommend.adapter.in.event.dto.payload.UserDeletedPayload;
import com.lxp.recommend.adapter.in.event.dto.payload.UserUpdatedPayload;
import com.lxp.recommend.application.dto.UserSyncCommand;
import com.lxp.recommend.application.port.in.UserSyncUseCase;
import com.lxp.recommend.global.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventListener {

    private static final String USER_CREATED = "user.created";
    private static final String USER_UPDATED = "user.updated";
    private static final String USER_DELETED = "user.deleted";

    private final UserSyncUseCase userSyncUseCase;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = RabbitMQConfig.USER_QUEUE)
    public void handle(IntegrationEvent event) {
        try {
            log.info("Received event: {}", event.eventId());
            processEvent(event);
        } catch (Exception e) {
            log.error("Failed to process event id: {}", event.eventId(), e);
            throw new RuntimeException(e);
        }
    }

    private void processEvent(IntegrationEvent event) throws Exception {
        switch (event.eventType()) {
            case USER_CREATED -> handleCreated(event);
            case USER_UPDATED -> handleUpdated(event);
            case USER_DELETED -> handleDeleted(event);
            default -> log.warn("Unknown event type: {}", event.eventType());
        }
    }

    private void handleCreated(IntegrationEvent event) throws Exception {
        UserCreatedPayload payload = parsePayload(event, UserCreatedPayload.class);
        userSyncUseCase.createUser(toCommand(event.eventId(), payload.userId(), payload.tagIds(), payload.level()));
    }

    private void handleUpdated(IntegrationEvent event) throws Exception {
        UserUpdatedPayload payload = parsePayload(event, UserUpdatedPayload.class);
        userSyncUseCase.updateUser(toCommand(event.eventId(), payload.userId(), payload.tagIds(), payload.level()));
    }

    private void handleDeleted(IntegrationEvent event) throws Exception {
        UserDeletedPayload payload = parsePayload(event, UserDeletedPayload.class);
        userSyncUseCase.deleteUser(event.eventId(), payload.userId());
    }

    private <T> T parsePayload(IntegrationEvent event, Class<T> payloadClass) throws Exception {
        return objectMapper.treeToValue(event.payload(), payloadClass);
    }

    private UserSyncCommand toCommand(String eventId, String userId, java.util.List<Long> tagIds, String level) {
        return new UserSyncCommand(eventId, userId, tagIds, level);
    }
}