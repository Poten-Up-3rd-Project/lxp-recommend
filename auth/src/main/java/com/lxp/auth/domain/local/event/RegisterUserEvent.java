package com.lxp.auth.domain.local.event;

import com.lxp.common.application.event.BaseIntegrationEvent;

import java.time.LocalDateTime;
import java.util.UUID;

public class RegisterUserEvent extends BaseIntegrationEvent {

    private final String eventType;
    private final Payload data;

    public RegisterUserEvent(String eventId, LocalDateTime occurredAt, String source, String correlationId, String causationId, int version, String eventType, Payload payload) {
        super(eventId, occurredAt, source, correlationId, causationId, version);
        this.eventType = eventType;
        this.data = payload;
    }

    public static RegisterUserEvent create(String eventId, String correlationId, String causationId, int version, UUID userId, String email, String role) {
        Payload payload = new Payload(userId, email, role);
        return new RegisterUserEvent(eventId, LocalDateTime.now(), "AUTH", correlationId, causationId, version, "REGISTER_USER", payload);
    }

    record Payload(UUID userId, String email, String role) {
    }
}
