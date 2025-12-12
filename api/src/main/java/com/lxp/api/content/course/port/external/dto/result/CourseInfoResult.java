package com.lxp.api.content.course.port.external.dto.result;

import com.lxp.common.enums.Level;

import java.util.List;

public record CourseInfoResult(
        String courseUUID,
        Long courseId,
        String instructorUUID,
        String title,
        String thumbnailUrl,
        String description,
        Long durationOnMinutes,
        Level difficulty,
        List<SectionInfoResult> sections,
        List<Long> tags
) {
    public record SectionInfoResult(
            String sectionUUID,
            Long sectionId,
            String title,
            int order,
            List<LectureInfoResult> lectures
    ) {

        public record LectureInfoResult(
                String lectureUUID,
                Long lectureId,
                String title,
                String videoUrl,
                Long durationOnSeconds,
                int order
        ) { }
    }
}
