package com.lxp.recommend.adapter.in.event.dto.payload;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserCreatedPayload(
        String userId,
        List<Long> tagIds,
        String level
) {
}
