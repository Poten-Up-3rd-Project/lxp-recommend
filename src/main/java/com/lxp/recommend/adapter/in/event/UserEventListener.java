package com.lxp.recommend.adapter.in.event;

import com.lxp.recommend.application.dto.UserSyncCommand;
import com.lxp.recommend.application.port.in.UserSyncUseCase;
import com.lxp.recommend.dto.event.UserEventPayload;
import com.lxp.recommend.global.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventListener {

    private final UserSyncUseCase userSyncUseCase;

    @RabbitListener(queues = RabbitMQConfig.USER_QUEUE)
    public void handle(UserEventPayload payload) {
        log.info("Received user event: type={}, userId={}", payload.eventType(), payload.userId());

        try {
            if (payload.isCreated()) {
                userSyncUseCase.createUser(toCommand(payload));
            } else if (payload.isUpdated()) {
                userSyncUseCase.updateUser(toCommand(payload));
            } else if (payload.isDeleted()) {
                userSyncUseCase.deleteUser(payload.userId());
            } else {
                log.warn("Unknown user event type: {}", payload.eventType());
            }
        } catch (Exception e) {
            log.error("Failed to process user event: {}", payload, e);
            throw e;
        }
    }

    private UserSyncCommand toCommand(UserEventPayload payload) {
        return new UserSyncCommand(
                payload.userId(),
                payload.interestTags(),
                payload.level()
        );
    }
}
