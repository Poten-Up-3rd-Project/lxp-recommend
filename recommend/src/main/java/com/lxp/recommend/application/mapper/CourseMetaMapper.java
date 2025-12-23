package com.lxp.recommend.application.mapper;

import com.lxp.common.enums.Level;  // ← 추가
import com.lxp.recommend.application.dto.CourseMetaData;
import com.lxp.recommend.domain.model.CourseCandidate;
import com.lxp.recommend.domain.model.ids.CourseId;

public class CourseMetaMapper {

    /**
     * Port DTO → Domain 변환
     */
    public static CourseCandidate toDomain(CourseMetaData data) {
        return new CourseCandidate(
                CourseId.of(data.courseId()),
                data.tags(),
                Level.valueOf(data.difficulty()),  // ← String → common.Level
                data.isPublic()
        );
    }
}
