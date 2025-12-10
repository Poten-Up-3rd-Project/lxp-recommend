package com.lxp.common.event.integration;

import com.lxp.common.application.event.IntegrationEvent;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CourseCreatedIntegrationEvent(
        String eventId,
        String courseUuid,
        String instructorUuid,
        String title,
        String description,
        String thumbnailUrl,
        String difficulty,
        List<Long> tagIds,
        LocalDateTime occurredAt
) implements IntegrationEvent {

    @Override
    public String getEventId() {
        return eventId;
    }

    @Override
    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    @Override
    public String getEventType() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getSource() {
        return "lxp.course.service";
    }

    @Override
    public int getVersion() {
        return IntegrationEvent.super.getVersion();
    }

    @Override
    public String getCorrelationId() {
        return UUID.randomUUID().toString();
    }

    @Override
    public String getCausationId() {
        return IntegrationEvent.super.getCausationId();
    }
}