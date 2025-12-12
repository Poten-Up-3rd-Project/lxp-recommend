package com.lxp.content.course.domain.service;

import com.lxp.common.domain.annotation.DomainService;
import com.lxp.common.domain.policy.BusinessRuleValidator;
import com.lxp.common.util.UUIdGenerator;
import com.lxp.api.content.course.port.usecase.dto.command.CourseCreateCommand;
import com.lxp.api.content.course.port.usecase.dto.command.LectureCreateCommand;
import com.lxp.content.course.application.port.required.dto.InstructorResult;
import com.lxp.content.course.domain.exception.CourseException;
import com.lxp.content.course.domain.model.Course;
import com.lxp.content.course.domain.model.Lecture;
import com.lxp.content.course.domain.model.Section;
import com.lxp.content.course.domain.model.collection.CourseSections;
import com.lxp.content.course.domain.model.collection.CourseTags;
import com.lxp.content.course.domain.model.collection.SectionLectures;
import com.lxp.content.course.domain.model.id.CourseUUID;
import com.lxp.content.course.domain.model.id.InstructorUUID;
import com.lxp.content.course.domain.model.id.LectureUUID;
import com.lxp.content.course.domain.model.id.SectionUUID;
import com.lxp.content.course.domain.model.id.TagId;
import com.lxp.content.course.domain.model.vo.duration.LectureDuration;
import com.lxp.content.course.domain.policy.SectionMinCountRule;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@DomainService
public class CourseCreateDomainService {

    public Course create(CourseCreateCommand command, InstructorResult instructor) {

        validateInstructorInfo(instructor);

        Course course =  Course.create(
                new CourseUUID(UUIdGenerator.createString()),
                new InstructorUUID(command.instructorId()),
                command.thumbnailUrl(),
                command.title(),
                command.description(),
                command.level(),
                createSections(command.sections()),
                createTags(command.tags())
        );

        BusinessRuleValidator.validate(new SectionMinCountRule(course.sections()));
        return course;
    }

    private static CourseSections createSections(List<CourseCreateCommand.SectionCreateCommand> commands) {
        if (commands == null || commands.isEmpty()) {
            return CourseSections.empty();
        }

        AtomicInteger order = new AtomicInteger(1);

        List<Section> sections = commands.stream().map(cmd ->
                Section.create(
                        cmd.title(),
                        new SectionUUID(UUIdGenerator.createString()),
                        order.getAndIncrement(),
                        createLectures(cmd.lectures())
                )
        ).collect(Collectors.toList());


        return CourseSections.of(sections);
    }

    private static SectionLectures createLectures(List<LectureCreateCommand> commands) {
        if (commands == null || commands.isEmpty()) {
            return SectionLectures.empty();
        }

        AtomicInteger order = new AtomicInteger(1);

        List<Lecture> lectures = commands.stream().map(cmd -> Lecture.create(
                        cmd.title(),
                        new LectureUUID(UUIdGenerator.createString()),
                        // TODO(추후 변경 필요)
                        LectureDuration.randomUnder20Minutes(),
                        order.getAndIncrement(),
                        cmd.videoUrl()
                )
        ).collect(Collectors.toList());

        return SectionLectures.of(lectures);
    }


    private static CourseTags createTags(List<Long> tagIds) {
        return CourseTags.of(
                tagIds.stream()
                        .map(TagId::new)
                        .toList()
        );
    }

    private void validateInstructorInfo(InstructorResult instructor) {
        if (!Objects.equals(instructor.status(), "ACTIVE")) {
            throw CourseException.InvalidInstructorException();
        }
        if (!Objects.equals(instructor.role(), "INSTRUCTOR")) {
            throw CourseException.InvalidInstructorException();
        }
    }
}
