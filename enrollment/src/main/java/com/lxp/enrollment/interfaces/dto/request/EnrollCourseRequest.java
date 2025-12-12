package com.lxp.enrollment.interfaces.dto.request;

public record EnrollCourseRequest(
        String userId,
        String courseId
) {
}
