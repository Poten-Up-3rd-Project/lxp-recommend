package com.lxp.recommend.infrastructure.external.course.dto;

import com.lxp.recommend.domain.model.ids.Level;

import java.time.Instant;
import java.util.List;

/**
 * Course BC로부터 받는 강좌 메타 정보
 */
public record CourseMetaResponse(
        String courseId,
        String title,
        String description,
        String thumbnailUrl,
        Level level,
        Instant createdAt,
        Instant updatedAt,
        List<CourseTag> tags
) {

    public record CourseTag(
            Long id,
            String content,
            String color,
            String variant
    ) {
    }
}
