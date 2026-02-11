package com.lxp.recommend.adapter.in.event.dto.payload;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CourseCreatedPayload(
        String courseUuid,
        String instructorUuid,
        String title,
        String description,
        String thumbnailUrl,
        String difficulty,
        List<Long> tagIds
) {}