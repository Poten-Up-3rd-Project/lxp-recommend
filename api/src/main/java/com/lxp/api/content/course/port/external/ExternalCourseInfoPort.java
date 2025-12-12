package com.lxp.api.content.course.port.external;

import com.lxp.api.content.course.port.external.dto.result.CourseInfoResult;

import java.util.Optional;

public interface ExternalCourseInfoPort {
    Optional<CourseInfoResult> getCourseInfo(String courseUUID);
}
