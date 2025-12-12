package com.lxp.content.course.interfaces.web.dto.response;

import java.util.List;

public record SectionResponse(
    String id,
    String title,
    long durationInSeconds,
    int order,
    List<LectureResponse> lectures
) {
}
