package com.lxp.recommend.infrastructure.messaging.consumer;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lxp.recommend.infrastructure.messaging.event.UserCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventConsumer {

    private final ObjectMapper objectMapper;

    @RabbitListener(queues = "user.events.queue")
    public void consume(String message) {
        log.info("Received: {}", message);

        try {
            JsonNode node = objectMapper.readTree(message);
            String eventType = node.get("eventType").asText();

            switch (eventType) {
                case "user.created" -> handleUserCreated(message);
                default -> log.warn("Unknown event type: {}", eventType);
            }

        } catch (Exception e) {
            log.error("Failed: {}", message, e);
            throw new RuntimeException(e);
        }
    }

    private void handleUserCreated(String message) throws Exception {
        var event = objectMapper.readValue(message, UserCreatedEvent.class);
        var payload = event.getPayload();

        log.info("User created - userId: {}, level: {}",
                payload.getUserId(),
                payload.getLevel());

        //TODO 추가 비즈니스 로직 구현
    }
}