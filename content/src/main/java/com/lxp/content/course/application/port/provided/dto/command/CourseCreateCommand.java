package com.lxp.content.course.application.port.provided.dto.command;

import com.lxp.common.application.cqrs.Command;
import com.lxp.content.course.domain.model.enums.CourseDifficulty;

import java.util.List;

public record CourseCreateCommand(
        String title,
        String description,
        String instructorId,
        String thumbnailUrl,
        CourseDifficulty level,
        List<Long> tags,
        List<SectionCreateCommand> sections
) implements Command {
    public record SectionCreateCommand(
            String title,
            List<LectureCreateCommand> lectures
    ) {}

}