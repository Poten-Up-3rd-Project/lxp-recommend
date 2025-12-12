package com.lxp.content.course.application.service;

import com.lxp.content.course.application.mapper.CourseResultMapper;
import com.lxp.api.content.course.port.external.ExternalCourseSummaryPort;
import com.lxp.api.content.course.port.external.dto.result.CourseResult;
import com.lxp.content.course.domain.model.Course;
import com.lxp.content.course.domain.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ExternalCourseSummaryService implements ExternalCourseSummaryPort {
    private final CourseRepository courseRepository;
    private final CourseResultMapper resultMapper;


    @Override
    public List<CourseResult> getCourseSummaryList(Collection<String> courseUUIDs) {
        List<Course> courses = courseRepository.findAllByUUID(courseUUIDs);
        return courses.stream().map(resultMapper::toResult).toList();
    }
}
