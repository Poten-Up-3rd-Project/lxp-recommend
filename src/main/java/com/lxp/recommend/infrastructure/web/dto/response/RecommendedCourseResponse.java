package com.lxp.recommend.infrastructure.web.dto.response;

import com.lxp.recommend.application.dto.RecommendedCourseDto;
import com.lxp.recommend.infrastructure.external.course.dto.CourseMetaResponse;

/**
 * External API 응답용 - 추천 강좌 정보
 */
public record RecommendedCourseResponse(
        CourseMetaResponse course,
        double score,
        int rank
) {

}
