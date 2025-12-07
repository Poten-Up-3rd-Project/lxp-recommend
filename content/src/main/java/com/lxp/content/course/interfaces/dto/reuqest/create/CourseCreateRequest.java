package com.lxp.content.course.interfaces.dto.reuqest.create;

import com.lxp.content.course.domain.model.enums.CourseDifficulty;

import java.util.List;

//TODO("valid 체크 ")
public record CourseCreateRequest(
    String title,
    String description,
    String instructorId,
    String thumbnailUrl,
    CourseDifficulty level,
    List<Long> tags,
    List<SectionCreateRequest> sections
) {
}
