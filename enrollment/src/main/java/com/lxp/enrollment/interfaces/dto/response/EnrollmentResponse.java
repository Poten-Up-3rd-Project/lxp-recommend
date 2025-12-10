package com.lxp.enrollment.interfaces.dto.response;

import java.util.List;

public record EnrollmentResponse(
    Long enrollmentId,
    String state, // ENROLLED, COMPLETED, CANCELLED
    String courseId,
    String thumbnailUrl,
    float totalProgress,
    String courseTitle,
    String courseDescription,
    String courseDifficulty,
    List<TagResponse> tags
) {

}
