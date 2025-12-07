package com.lxp.content.course.interfaces.dto.response;

import com.lxp.content.course.domain.model.enums.CourseDifficulty;

import java.util.List;

public record CourseResponse(
        String id,
        String title,
        String description,
        String instructorName,
        CourseDifficulty level,
        long durationInHours,
        // TODO("추후 tagResponse로 정의 - name 필요")
        List<Long> tags,
        List<SectionResponse> section

) {
}
