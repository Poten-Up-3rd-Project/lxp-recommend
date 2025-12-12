package com.lxp.content.course.interfaces.web.dto.reuqest.create;

import java.util.List;

public record SectionCreateRequest(
    String title,
    List<LectureCreateRequest> lectures
) {
}
