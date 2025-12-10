package com.lxp.content.course.domain.event;

import com.lxp.common.domain.event.BaseDomainEvent;
import com.lxp.content.course.domain.model.enums.CourseDifficulty;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class CourseCreatedEvent extends BaseDomainEvent {

    private final String instructorUuid;
    private final String title;
    private final String description;
    private final String thumbnailUrl;
    private final CourseDifficulty difficulty;
    private final List<Long> tagIds;

    public CourseCreatedEvent(String courseUuid, String instructorUuid, String title, String description, String thumbnailUrl, CourseDifficulty difficulty, List<Long> tagIds) {
        super(courseUuid);
        this.instructorUuid = instructorUuid;
        this.title = title;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
        this.difficulty = difficulty;
        this.tagIds = tagIds;
    }

    public CourseCreatedEvent(String eventId, LocalDateTime occurredAt, String courseUuid, String instructorUuid, String title, String description, String thumbnailUrl, CourseDifficulty difficulty, List<Long> tagIds) {
        super(eventId, courseUuid, occurredAt);
        this.instructorUuid = instructorUuid;
        this.title = title;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
        this.difficulty = difficulty;
        this.tagIds = tagIds;
    }
}
