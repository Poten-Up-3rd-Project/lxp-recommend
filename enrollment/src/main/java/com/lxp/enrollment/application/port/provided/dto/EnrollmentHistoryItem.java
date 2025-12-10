package com.lxp.enrollment.application.port.provided.dto;

import java.time.LocalDateTime;
import java.util.List;

public record EnrollmentHistoryItem(
    Long enrollmentId,
    String state, // ENROLLED, COMPLETED, CANCELLED
    String courseId,
    String thumbnailUrl,
    float totalProgress,
    String courseTitle,
    String courseDescription,
    List<TagResult> level,
    LocalDateTime enrolledAt
) {

}
