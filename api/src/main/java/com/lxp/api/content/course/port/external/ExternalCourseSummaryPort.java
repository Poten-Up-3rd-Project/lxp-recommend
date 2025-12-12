package com.lxp.api.content.course.port.external;

import com.lxp.api.content.course.port.external.dto.result.CourseResult;

import java.util.Collection;
import java.util.List;

public interface ExternalCourseSummaryPort {
    List<CourseResult> getCourseSummaryList(Collection<String> courseUUIDs);
}
