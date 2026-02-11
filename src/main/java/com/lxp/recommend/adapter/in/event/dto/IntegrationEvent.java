package com.lxp.recommend.adapter.in.event.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record IntegrationEvent(
        String eventId,
        String eventType,
        LocalDateTime occurredAt,
        String source,
        String correlationId,
        String causationId,
        JsonNode payload
) {
}