package com.lxp.user.presentation.rest.dto.request;

import java.util.List;

public record UserUpdateRequest(String name, String level, List<Long> tagIds, String job) {
}
