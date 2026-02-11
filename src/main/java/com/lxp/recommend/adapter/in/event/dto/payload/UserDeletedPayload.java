package com.lxp.recommend.adapter.in.event.dto.payload;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserDeletedPayload(
        String userId
) {
}
