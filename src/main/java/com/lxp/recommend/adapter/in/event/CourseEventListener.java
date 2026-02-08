package com.lxp.recommend.adapter.in.event;

import com.lxp.recommend.application.dto.CourseSyncCommand;
import com.lxp.recommend.application.port.in.CourseSyncUseCase;
import com.lxp.recommend.dto.event.CourseEventPayload;
import com.lxp.recommend.global.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CourseEventListener {

    private final CourseSyncUseCase courseSyncUseCase;

    @RabbitListener(queues = RabbitMQConfig.COURSE_QUEUE)
    public void handle(CourseEventPayload payload) {
        log.info("Received course event: type={}, courseId={}", payload.eventType(), payload.courseId());

        try {
            if (payload.isCreated()) {
                courseSyncUseCase.createCourse(toCommand(payload));
            } else if (payload.isDeleted()) {
                courseSyncUseCase.deleteCourse(payload.courseId());
            } else {
                log.warn("Unknown course event type: {}", payload.eventType());
            }
        } catch (Exception e) {
            log.error("Failed to process course event: {}", payload, e);
            throw e;
        }
    }

    private CourseSyncCommand toCommand(CourseEventPayload payload) {
        return new CourseSyncCommand(
                payload.courseId(),
                payload.tags(),
                payload.level(),
                payload.instructorId()
        );
    }
}
