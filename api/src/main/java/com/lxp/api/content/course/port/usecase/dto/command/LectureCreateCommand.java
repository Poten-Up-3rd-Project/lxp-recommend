package com.lxp.api.content.course.port.usecase.dto.command;

public record LectureCreateCommand(
        String title,
        String videoUrl
) {
}
