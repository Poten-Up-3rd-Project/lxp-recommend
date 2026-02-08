package com.lxp.recommend.application.dto;

import java.util.List;

public record CourseSyncCommand(
        String courseId,
        List<Long> tags,
        String level,
        String instructorId
) {
}
