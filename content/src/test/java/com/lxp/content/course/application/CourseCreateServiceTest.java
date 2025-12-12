package com.lxp.content.course.application;


import com.lxp.api.content.course.port.usecase.dto.result.CourseDetailView;
import com.lxp.api.content.course.port.usecase.dto.result.InstructorView;
import com.lxp.api.content.course.port.usecase.dto.result.TagInfoView;
import com.lxp.common.application.port.out.DomainEventPublisher;
import com.lxp.common.enums.Level;
import com.lxp.content.course.application.mapper.CourseResultMapper;
import com.lxp.api.content.course.port.usecase.dto.command.CourseCreateCommand;
import com.lxp.api.content.course.port.usecase.dto.command.LectureCreateCommand;
import com.lxp.api.content.course.port.external.dto.result.CourseInfoResult;
import com.lxp.content.course.application.port.required.TagQueryPort;
import com.lxp.content.course.application.port.required.UserQueryPort;
import com.lxp.content.course.application.port.required.dto.InstructorResult;
import com.lxp.content.course.application.port.required.dto.TagResult;
import com.lxp.content.course.application.service.CourseCreateService;
import com.lxp.content.course.domain.event.CourseCreatedEvent;
import com.lxp.content.course.domain.model.Course;
import com.lxp.content.course.domain.model.collection.CourseSections;
import com.lxp.content.course.domain.model.collection.CourseTags;
import com.lxp.content.course.domain.model.id.CourseUUID;
import com.lxp.content.course.domain.model.id.InstructorUUID;
import com.lxp.content.course.domain.model.id.TagId;
import com.lxp.content.course.domain.repository.CourseRepository;
import com.lxp.content.course.domain.service.CourseCreateDomainService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CourseCreateServiceTest {


    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CourseCreateDomainService courseCreateDomainService;

    @Mock
    private CourseResultMapper resultMapper;

    @Mock
    private DomainEventPublisher domainEventPublisher;

    @Mock
    private UserQueryPort userQueryPort;

    @InjectMocks
    private CourseCreateService courseCreateService;

    @Mock
    private TagQueryPort tagQueryPort;


    @Test
    @DisplayName("강의 생성 시 도메인 이벤트가 발행된다")
    void handle_ShouldPublishDomainEvent() {
        // Given
        CourseCreateCommand command = createCommand();
        Course course = createCourseWithEvent();  // 이벤트 포함
        CourseDetailView expectedResult = createExpectedResult();
        InstructorResult instructorInfo = createInstructorInfo();
        List<TagResult> tagResults = List.of();

        when(userQueryPort.getInstructorInfo(command.instructorId())).thenReturn(instructorInfo);
        when(courseCreateDomainService.create(command, instructorInfo)).thenReturn(course);
        when(courseRepository.save(course)).thenReturn(course);  // 같은 객체 반환
        when(tagQueryPort.findTagByIds(command.tags())).thenReturn(tagResults);
        when(resultMapper.toCourseDetailView(course, tagResults, instructorInfo)).thenReturn(expectedResult);

        // When
        courseCreateService.handle(command);

        // Then
        verify(domainEventPublisher, times(1)).publish(any(CourseCreatedEvent.class));
    }

    private InstructorResult createInstructorInfo() {
        return new InstructorResult(
                "instructor-uuid",
                "강사 이름",
                "INSTRUCTOR",
               "JUNIOR"
        );
    }

    private CourseCreateCommand createCommand() {
        return new CourseCreateCommand(
                "instructor-uuid",
                "https://example.com/thumbnail.jpg",
                "테스트 강의",
                "테스트 설명",
                Level.JUNIOR,
                List.of(1L, 2L),
                List.of(new CourseCreateCommand.SectionCreateCommand(
                        "섹션 1",
                        List.of(new LectureCreateCommand("강의 1", "https://video.url"))
                ))
        );
    }

    private Course createCourseWithEvent() {
        return Course.create(
                new CourseUUID(UUID.randomUUID().toString()),
                new InstructorUUID("instructor-uuid"),
                "https://example.com/thumbnail.jpg",
                "테스트 강의",
                "테스트 설명",
                Level.JUNIOR,
                CourseSections.empty(),
                CourseTags.of(List.of(new TagId(1L)))
        );
    }

    private Course createSavedCourse() {
        return Course.reconstruct(
                1L,
                new CourseUUID(UUID.randomUUID().toString()),
                new InstructorUUID("instructor-uuid"),
                "https://example.com/thumbnail.jpg",
                "테스트 강의",
                "테스트 설명",
                Level.JUNIOR,
                CourseSections.empty(),
                CourseTags.of(List.of(new TagId(1L))),
                Instant.now(),
                Instant.now()
        );
    }

    private CourseDetailView createExpectedResult() {
        return new CourseDetailView(
                UUID.randomUUID().toString(),
                "테스트 강의",
                "테스트 설명",
                new InstructorView("instructor-uuid", "강사 이름"),
                "https://example.com/thumbnail.jpg",
                Level.JUNIOR,
                List.of(), // sections
                List.of(new TagInfoView(1L, "tag-content", "#fff", "basic")),
                Instant.now(), // createdAt
                Instant.now(), // updatedAt
                3000L
        );
    }


}
