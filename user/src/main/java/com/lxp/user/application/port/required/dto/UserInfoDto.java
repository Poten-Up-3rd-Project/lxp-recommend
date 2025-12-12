package com.lxp.user.application.port.required.dto;

import java.util.List;

public record UserInfoDto(
    String id,
    String name,
    String email,
    List<Long> tags,
    String level,
    String job
) {
}
