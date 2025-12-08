package com.lxp.content.course.application.port.provided.external;

import com.lxp.content.course.application.port.provided.dto.result.CourseResult;

import java.util.Optional;

public interface ExternalCourseSummaryPort {
    Optional<CourseResult> getCourseSummary(String courseUUID);
}
