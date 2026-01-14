package com.lxp.recommend.infrastructure.external.course.dto;

import java.util.List;

/**
 * Course BC로부터 받는 강좌 메타 정보
 */
public record CourseMetaResponse(
        String courseId,
        List<String> tags,
        String difficulty,
        boolean isPublic
) {
}
