package com.lxp.recommend.adapter.in.event.dto.payload;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CourseDeletedPayload(
        String courseUuid,
        LocalDateTime deletedAt
) {}