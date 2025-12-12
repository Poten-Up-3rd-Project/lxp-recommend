package com.lxp.content.course.interfaces.web.dto.reuqest.update;


import com.lxp.common.enums.Level;

import java.util.List;

public record CourseUpdateRequest(
        String title,
        String description,
        String thumbnailUrl,
        Level level,
        List<Long> tags
) {
}
