package com.lxp.content.course.application.port.provided.dto.result;

import com.lxp.content.course.domain.model.enums.CourseDifficulty;

import java.util.List;

public record CourseInfoResult(
        String courseUUID,
        Long courseId,
        String instructorUUID,
        String title,
        String thumbnailUrl,
        String description,
        Long durationOnMinutes,
        CourseDifficulty difficulty,
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
