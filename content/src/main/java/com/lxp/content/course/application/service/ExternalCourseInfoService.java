package com.lxp.content.course.application.service;

import com.lxp.content.course.application.mapper.CourseResultMapper;
import com.lxp.api.content.course.port.external.dto.result.CourseInfoResult;
import com.lxp.api.content.course.port.external.ExternalCourseInfoPort;
import com.lxp.content.course.domain.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ExternalCourseInfoService implements ExternalCourseInfoPort {
    private final CourseRepository courseRepository;
    private final CourseResultMapper resultMapper;


    @Override
    public Optional<CourseInfoResult> getCourseInfo(String courseUUID) {
        return courseRepository.findByUUID(courseUUID).map(resultMapper::toInfoResult);
    }
}
