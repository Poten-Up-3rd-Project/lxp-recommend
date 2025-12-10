package com.lxp.content.course.application.port.provided.dto.command;

public record LectureCreateCommand(
        String title,
        String videoUrl
) {
}
