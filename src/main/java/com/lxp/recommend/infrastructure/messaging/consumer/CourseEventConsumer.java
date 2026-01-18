package com.lxp.recommend.infrastructure.messaging.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lxp.recommend.infrastructure.messaging.event.CourseCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CourseEventConsumer {

    private final ObjectMapper objectMapper;

    @RabbitListener(queues = "course.events")
    public void consume(String message) {
        log.info("Received: {}", message);

        try {
            JsonNode node = objectMapper.readTree(message);
            String eventType = node.get("eventType").asText();

            switch (eventType) {
                case "course.created" -> handleCourseCreated(message);
                default -> log.warn("Unknown event type: {}", eventType);
            }

        } catch (Exception e) {
            log.error("Failed: {}", message, e);
            throw new RuntimeException(e);
        }
    }

    private void handleCourseCreated(String message) throws Exception {
        var event = objectMapper.readValue(message, CourseCreatedEvent.class);
        var payload = event.getPayload();

        log.info("Course created - uuid: {}, title: {}",
                payload.getCourseUuid(),
                payload.getTitle());

        //TODO 추가 비즈니스 로직 구현
    }
}