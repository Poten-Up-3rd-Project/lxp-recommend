package com.lxp.content.course.interfaces.web.dto.response;

public record LectureResponse(
        String id,
        String title,
        String videoUrl,
        int order,
        long durationInSeconds

) {
}
