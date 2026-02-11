package com.lxp.recommend.application.dto;

public record EnrollSyncCommand(
        String eventId,
        String userId,
        String courseId
) {
}
