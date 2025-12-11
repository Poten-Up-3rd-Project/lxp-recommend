package com.lxp.api.user.port.dto.result;

import java.util.List;

public record UserInfoResponse(
    String userId,
    String name,
    String email,
    String role,
    String status,
    ProfileDto profile
) {
    public record ProfileDto(
        List<Long> tags,
        String level,
        String job
    ) {
    }
}
