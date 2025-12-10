package com.lxp.content.course.application;


import com.lxp.common.application.port.out.DomainEventPublisher;
import com.lxp.content.course.application.mapper.CourseResultMapper;
import com.lxp.content.course.application.port.provided.dto.command.CourseCreateCommand;
import com.lxp.content.course.application.port.provided.dto.command.LectureCreateCommand;
import com.lxp.content.course.application.port.provided.dto.result.CourseInfoResult;
import com.lxp.content.course.application.service.CourseCreateService;
import com.lxp.content.course.domain.event.CourseCreatedEvent;
import com.lxp.content.course.domain.model.Course;
import com.lxp.content.course.domain.model.collection.CourseSections;
import com.lxp.content.course.domain.model.collection.CourseTags;
import com.lxp.content.course.domain.model.enums.CourseDifficulty;
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

    @InjectMocks
    private CourseCreateService courseCreateService;




    @Test
    @DisplayName("강의 생성 시 도메인 이벤트가 발행된다")
    void handle_ShouldPublishDomainEvent() {
        // Given
        CourseCreateCommand command = createCommand();
        Course course = createCourseWithEvent();  // 이벤트 포함
        CourseInfoResult expectedResult = createExpectedResult();

        when(courseCreateDomainService.create(command)).thenReturn(course);
        when(courseRepository.save(course)).thenReturn(course);
        when(resultMapper.toInfoResult(course)).thenReturn(expectedResult);

        // When
        courseCreateService.handle(command);

        // Then
        verify(domainEventPublisher, times(1)).publish(any(CourseCreatedEvent.class));
    }

    private CourseCreateCommand createCommand() {
        return new CourseCreateCommand(
                "instructor-uuid",
                "https://example.com/thumbnail.jpg",
                "테스트 강의",
                "테스트 설명",
                CourseDifficulty.JUNIOR,
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
                CourseDifficulty.JUNIOR,
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
                CourseDifficulty.JUNIOR,
                CourseSections.empty(),
                CourseTags.of(List.of(new TagId(1L))),
                Instant.now(),
                Instant.now()
        );
    }

    private CourseInfoResult createExpectedResult() {
        return new CourseInfoResult(
                UUID.randomUUID().toString(),
                1L,
                "instructor-uuid",
                "테스트 강의",
                "https://example.com/thumbnail.jpg",
                "테스트 설명",
                3000L,
                CourseDifficulty.JUNIOR,
                List.of(),
                List.of(1L)
        );
    }


}
