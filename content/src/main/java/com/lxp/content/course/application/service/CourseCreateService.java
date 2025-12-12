package com.lxp.content.course.application.service;

import com.lxp.api.content.course.port.usecase.dto.result.CourseDetailView;
import com.lxp.common.application.port.out.DomainEventPublisher;
import com.lxp.content.course.application.mapper.CourseResultMapper;
import com.lxp.api.content.course.port.usecase.dto.command.CourseCreateCommand;
import com.lxp.content.course.application.port.provided.usecase.CourseCreateUseCase;
import com.lxp.content.course.application.port.required.TagQueryPort;
import com.lxp.content.course.application.port.required.UserQueryPort;
import com.lxp.content.course.application.port.required.dto.InstructorResult;
import com.lxp.content.course.application.port.required.dto.TagResult;
import com.lxp.content.course.domain.model.Course;
import com.lxp.content.course.domain.repository.CourseRepository;
import com.lxp.content.course.domain.service.CourseCreateDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional
@RequiredArgsConstructor
public class CourseCreateService implements CourseCreateUseCase {
    private final CourseRepository courseRepository;
    private final CourseCreateDomainService courseCreateDomainService;
    private final CourseResultMapper resultMapper;
    private final DomainEventPublisher domainEventPublisher;
    private final UserQueryPort userQueryPort;
    private final TagQueryPort tagQueryPort;

    @Override
    public CourseDetailView handle(CourseCreateCommand command) {

        InstructorResult instructorInfo = userQueryPort.getInstructorInfo(command.instructorId());

        Course course = courseCreateDomainService.create(command,instructorInfo);
        courseRepository.save(course);

        List<TagResult> tagResults = tagQueryPort.findTagByIds(command.tags());

        course.getDomainEvents().forEach(domainEventPublisher::publish);
        course.clearDomainEvents();

        return resultMapper.toCourseDetailView(course,tagResults,instructorInfo);
    }
}
