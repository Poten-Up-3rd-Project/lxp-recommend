package com.lxp.content.course.interfaces.web.mapper;

import com.lxp.api.content.course.port.usecase.dto.command.CourseCreateCommand;
import com.lxp.api.content.course.port.usecase.dto.command.LectureCreateCommand;
import com.lxp.api.content.course.port.usecase.dto.result.CourseDetailView;
import com.lxp.content.course.interfaces.web.dto.response.*;
import com.lxp.content.course.interfaces.web.dto.reuqest.create.CourseCreateRequest;
import com.lxp.content.course.interfaces.web.dto.reuqest.create.LectureCreateRequest;
import com.lxp.content.course.interfaces.web.dto.reuqest.create.SectionCreateRequest;
import com.lxp.common.response.EnumResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CourseWebMapper {
    public CourseCreateCommand toCreateCommand(CourseCreateRequest request) {

        return new CourseCreateCommand(
                request.title(),
                request.description(),
                request.instructorId(),
                request.thumbnailUrl(),
                request.level(),
                request.tags(),
                request.sections().stream()
                        .map(this::toSectionCommand)
                        .collect(Collectors.toList())
        );
    }


    private CourseCreateCommand.SectionCreateCommand toSectionCommand(SectionCreateRequest sec) {
        List<LectureCreateCommand> lectures = sec.lectures().stream()
                .map(this::toLectureCommand)
                .collect(Collectors.toList());
        return new CourseCreateCommand.SectionCreateCommand(
                sec.title(),
                lectures
        );
    }

    private LectureCreateCommand toLectureCommand(LectureCreateRequest lec) {
        return new LectureCreateCommand(
                lec.title(),
                lec.videoUrl()
        );
    }

    public CourseDetailResponse toDetailResponse(CourseDetailView result) {
        EnumResponse level = new EnumResponse(result.level().name(), result.level().description());

        List<TagResponse> tags = (result.tags() == null) ? List.of() : result.tags().stream()
                .map(t -> new TagResponse(t.id(), t.content(), t.color(), t.variant()))
                .collect(Collectors.toList());

        List<SectionResponse> sections = (result.sections() == null) ? List.of() : result.sections().stream()
                .map(this::toSectionResponse)
                .collect(Collectors.toList());

        var instructor = new InstructorResponse(result.Instructor().instructorId(), result.Instructor().name());

        return new CourseDetailResponse(
                result.courseId(),
                instructor,
                result.title(),
                result.description(),
                result.thumbnailUrl(),
                level,
                tags,
                sections,
                result.durationOnMinutes()
        );
    }

    private SectionResponse toSectionResponse(CourseDetailView.SectionView sec) {

        return new SectionResponse(
                sec.sectionId(),
                sec.title(),
                sec.durationOnSecond(),
                sec.order(),
                sec.lectures().stream()
                        .map(this::toLectureResponse)
                        .collect(Collectors.toList())
        );
    }

    private LectureResponse toLectureResponse(CourseDetailView.SectionView.LectureView lect) {
        return new LectureResponse(lect.lectureId(), lect.title(),lect.videoUrl(),lect.order(),lect.duration());
    }


}
