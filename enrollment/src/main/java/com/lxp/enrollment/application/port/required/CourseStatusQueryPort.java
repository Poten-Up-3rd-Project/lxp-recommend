package com.lxp.enrollment.application.port.required;

public interface CourseStatusQueryPort {
    boolean isActiveCourse(String courseId);
}
