package com.lxp.recommend.application.dto;

import java.util.List;

public record UserSyncCommand(
        String eventId,
        String userId,
        List<Long> interestTags,
        String level
) {
}
