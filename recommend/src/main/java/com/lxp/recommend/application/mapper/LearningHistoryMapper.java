package com.lxp.recommend.application.mapper;

import com.lxp.recommend.application.dto.LearningHistoryData;
import com.lxp.recommend.domain.model.ids.EnrollmentStatus;
import com.lxp.recommend.infrastructure.external.enrollment.LearningStatusView;

public class LearningHistoryMapper {

    public static LearningStatusView toDomain(LearningHistoryData data) {
        return new LearningStatusView(
                data.learnerId(),
                data.courseId(),
                EnrollmentStatus.valueOf(data.status())
        );
    }
}
