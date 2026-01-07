package com.lxp.recommend.infrastructure.external.course;

import com.lxp.recommend.domain.model.ids.Level;
import java.util.Set;

public record CourseMetaView(
        String courseId,          // UUID -> Long
        Set<String> tags,
        Level difficulty,
        boolean isPublic
) {}