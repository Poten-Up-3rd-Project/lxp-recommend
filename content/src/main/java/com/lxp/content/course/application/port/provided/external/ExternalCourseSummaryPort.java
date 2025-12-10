package com.lxp.content.course.application.port.provided.external;

import com.lxp.content.course.application.port.provided.dto.result.CourseResult;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ExternalCourseSummaryPort {
    List<CourseResult> getCourseSummaryList(Collection<String> courseUUIDs);
}
