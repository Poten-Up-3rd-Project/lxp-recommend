package com.lxp.recommend.adapter.in.event;

import com.lxp.recommend.application.dto.EnrollSyncCommand;
import com.lxp.recommend.application.port.in.EnrollSyncUseCase;
import com.lxp.recommend.dto.event.EnrollEventPayload;
import com.lxp.recommend.global.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EnrollEventListener {

    private final EnrollSyncUseCase enrollSyncUseCase;

    @RabbitListener(queues = RabbitMQConfig.ENROLL_QUEUE)
    public void handle(EnrollEventPayload payload) {
        log.info("Received enroll event: type={}, userId={}, courseId={}",
                payload.eventType(), payload.userId(), payload.courseId());

        try {
            EnrollSyncCommand command = new EnrollSyncCommand(payload.userId(), payload.courseId());

            if (payload.isCreated()) {
                enrollSyncUseCase.createEnrollment(command);
            } else if (payload.isDeleted()) {
                enrollSyncUseCase.deleteEnrollment(command);
            } else {
                log.warn("Unknown enroll event type: {}", payload.eventType());
            }
        } catch (Exception e) {
            log.error("Failed to process enroll event: {}", payload, e);
            throw e;
        }
    }
}
