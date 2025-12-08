package com.lxp.content.course.interfaces.dto.response;

import com.lxp.common.response.EnumResponse;
import java.util.List;

public record CourseResponse(
        String id,
        String title,
        String description,
        String instructorName,
        // 추후 매핑 작업
        EnumResponse level, // key: JUNIOR value : 주니어
        long durationInHours,
        List<TagResponse> tags,
        List<SectionResponse> section
) {
}
