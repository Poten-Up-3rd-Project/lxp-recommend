package com.lxp.content.course.application.port.provided.external;

import com.lxp.content.course.application.port.provided.dto.result.CourseResult;

public interface ExternalCourseSummaryPort {
    CourseResult getCourseSummary(String courseUUID);
}
