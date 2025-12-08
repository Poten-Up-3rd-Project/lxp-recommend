package com.lxp.content.course.interfaces.dto.response;

import com.lxp.common.response.EnumResponse;
import java.util.List;

public record CourseSummaryResponse(
    String id,
    String instructorName,
    String title,
    String description,
    String thumbnailUrl,
    EnumResponse level,
    List<TagResponse> tags
) {
}
