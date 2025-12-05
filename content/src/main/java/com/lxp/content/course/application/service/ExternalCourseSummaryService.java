package com.lxp.content.course.application.service;

import com.lxp.content.course.application.port.provided.external.ExternalCourseSummaryPort;
import com.lxp.content.course.application.port.provided.dto.result.CourseResult;
import org.springframework.stereotype.Service;

@Service
public class ExternalCourseSummaryService implements ExternalCourseSummaryPort {
    @Override
    public CourseResult getCourseSummary(String courseUUID) {
        return null;
    }
}
