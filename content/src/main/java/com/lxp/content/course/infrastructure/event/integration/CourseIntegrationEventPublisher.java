package com.lxp.content.course.infrastructure.event.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lxp.common.application.event.IntegrationEvent;
import com.lxp.common.application.port.out.IntegrationEventPublisher;
import com.lxp.common.infrastructure.persistence.OutboxEvent;
import com.lxp.common.infrastructure.persistence.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CourseIntegrationEventPublisher implements IntegrationEventPublisher {

    //TODO: 추후 메시지 브로커 연동 시 수정 필요
    // outbox 패턴도입 고려
    private final ApplicationEventPublisher eventPublisher;

    private final OutboxEventRepository outboxEventRepository;

    private final ObjectMapper objectMapper;

    @Override
    public void publish(IntegrationEvent event) {
        OutboxEvent outboxEvent = new OutboxEvent(
                event.getEventType(),      // eventType
                event.getSource(),         // aggregateType
                event.getEventId(),        // aggregateId
                toJson(event)              // payload
        );
        outboxEventRepository.save(outboxEvent);

        // 즉시 발행 (추후 메시지 브로커 연동 시 수정 필요)
        // -> 스케줄러로 메세지 브로커에 발행하도록 변경 예정
        try {
            eventPublisher.publishEvent(event);
            outboxEvent.markAsPublished();
        } catch (Exception e) {
            outboxEvent.markAsFailed(e.getMessage());
        }

    }

    @Override
    public void publish(String topic, IntegrationEvent event) {
        return;
    }

    private String toJson(IntegrationEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize event", e);
        }
    }
}
