package com.lxp.enrollment.infrastructure.external.adapter;

import com.lxp.api.content.course.port.external.ExternalCourseInfoPort;
import com.lxp.enrollment.application.port.required.CourseStatusQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CourseStatusQueryAdapter implements CourseStatusQueryPort {
    private final ExternalCourseInfoPort externalCourseInfoPort;

    @Override
    public boolean isActiveCourse(String courseId) {
        return externalCourseInfoPort.getCourseInfo(courseId).isPresent();
    }
}
