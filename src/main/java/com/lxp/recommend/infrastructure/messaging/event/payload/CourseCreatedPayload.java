package com.lxp.recommend.infrastructure.messaging.event.payload;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class CourseCreatedPayload {
    private String courseUuid;
    private String instructorUuid;
    private String title;
    private String description;
    private String thumbnailUrl;
    private String difficulty;
    private List<Long> tagIds;
}