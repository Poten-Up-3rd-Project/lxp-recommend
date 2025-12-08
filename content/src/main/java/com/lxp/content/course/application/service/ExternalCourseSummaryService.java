package com.lxp.content.course.application.service;

import com.lxp.content.course.application.port.provided.external.ExternalCourseSummaryPort;
import com.lxp.content.course.application.port.provided.dto.result.CourseResult;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ExternalCourseSummaryService implements ExternalCourseSummaryPort {
    @Override
    public Optional<CourseResult> getCourseSummary(String courseUUID) {
        return null;
    }
}
