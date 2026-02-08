package com.lxp.recommend.application.dto;

public record EnrollSyncCommand(
        String userId,
        String courseId
) {
}
