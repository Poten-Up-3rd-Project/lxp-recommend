package com.lxp.auth.infrastructure.messaging;

import com.lxp.common.application.event.IntegrationEvent;
import com.lxp.common.application.port.out.IntegrationEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthIntegrationPublisher implements IntegrationEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void publish(IntegrationEvent integrationEvent) {
        eventPublisher.publishEvent(integrationEvent);
    }

    @Override
    public void publish(String s, IntegrationEvent integrationEvent) {
        eventPublisher.publishEvent(integrationEvent);
    }
}
