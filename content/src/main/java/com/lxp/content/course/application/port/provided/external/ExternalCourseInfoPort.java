package com.lxp.content.course.application.port.provided.external;

import com.lxp.content.course.application.port.provided.dto.result.CourseInfoResult;

import java.util.Optional;

public interface ExternalCourseInfoPort {
    Optional<CourseInfoResult> getCourseInfo(String courseUUID);
}
