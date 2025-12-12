package com.lxp.content.course.domain.service;

import com.lxp.api.content.course.port.usecase.dto.command.CourseCreateCommand;
import com.lxp.api.content.course.port.usecase.dto.command.LectureCreateCommand;
import com.lxp.common.enums.Level;
import com.lxp.content.course.application.port.required.dto.InstructorResult;
import com.lxp.content.course.domain.exception.CourseException;
import com.lxp.content.course.domain.model.Course;
import com.lxp.content.course.domain.model.Section;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CourseCreateDomainServiceTest {
    private CourseCreateDomainService domainService;

    @BeforeEach
    void setUp() {
        domainService = new CourseCreateDomainService();
    }

    @Nested
    @DisplayName("Course 생성 성공")
    class CreateSuccess {

        @Test
        @DisplayName("정상적인 Course 생성")
        void create_Success() {
            // Given
            CourseCreateCommand command = createValidCommand();
            InstructorResult instructor = createActiveInstructor();

            // When
            Course course = domainService.create(command, instructor);

            // Then
            assertThat(course).isNotNull();
            assertThat(course.uuid()).isNotNull();
            assertThat(course.title().value()).isEqualTo("테스트 강좌");
            assertThat(course.description().value()).isEqualTo("테스트 설명");
            assertThat(course.level()).isEqualTo(Level.JUNIOR);
        }

        @Test
        @DisplayName("Section과 Lecture가 포함된 Course 생성")
        void create_WithSectionsAndLectures() {
            // Given
            CourseCreateCommand command = createCommandWithSectionsAndLectures();
            InstructorResult instructor = createActiveInstructor();

            // When
            Course course = domainService.create(command, instructor);

            // Then
            assertThat(course.sections().values().size()).isEqualTo(2);
        }

        @Test
        @DisplayName("Tag가 포함된 Course 생성")
        void create_WithTags() {
            // Given
            CourseCreateCommand command = createValidCommand();
            InstructorResult instructor = createActiveInstructor();

            // When
            Course course = domainService.create(command, instructor);

            // Then
            assertThat(course.tags().values().size()).isEqualTo(3);
        }

        @Test
        @DisplayName("Section 순서가 올바르게 설정됨")
        void create_SectionOrderIsCorrect() {
            // Given
            CourseCreateCommand command = createCommandWithSectionsAndLectures();
            InstructorResult instructor = createActiveInstructor();

            // When
            Course course = domainService.create(command, instructor);

            // Then
            List<Integer> orders = course.sections().values().stream()
                    .map(Section::order)
                    .toList();
            assertThat(orders).containsExactly(1, 2);
        }
    }

    @Nested
    @DisplayName("Course 생성 실패 - Instructor 검증")
    class CreateFailInstructor {

        @Test
        @DisplayName("Instructor 상태가 ACTIVE가 아니면 예외 발생")
        void create_InstructorNotActive_ThrowsException() {
            // Given
            CourseCreateCommand command = createValidCommand();
            InstructorResult instructor = new InstructorResult(
                    "instructor-uuid",
                    "강사이름",
                    "INSTRUCTOR",
                    "INACTIVE"
            );

            // When & Then
            assertThatThrownBy(() -> domainService.create(command, instructor))
                    .isInstanceOf(CourseException.class);
        }

        @Test
        @DisplayName("Instructor 역할이 INSTRUCTOR가 아니면 예외 발생")
        void create_InstructorRoleInvalid_ThrowsException() {
            // Given
            CourseCreateCommand command = createValidCommand();
            InstructorResult instructor = new InstructorResult(
                    "instructor-uuid",
                    "ACTIVE",
                    "STUDENT" ,
                    "ACTIVE"
            );

            // When & Then
            assertThatThrownBy(() -> domainService.create(command, instructor))
                    .isInstanceOf(CourseException.class);
        }
    }

    @Nested
    @DisplayName("Course 생성 실패 - Section 검증")
    class CreateFailSection {

        @Test
        @DisplayName("Section이 없으면 예외 발생")
        void create_NoSections_ThrowsException() {
            // Given
            CourseCreateCommand command = createCommandWithoutSections();
            InstructorResult instructor = createActiveInstructor();

            // When & Then
            assertThatThrownBy(() -> domainService.create(command, instructor))
                    .isInstanceOf(RuntimeException.class);  // BusinessRuleException 또는 해당 예외
        }
    }

    // === Test Fixtures ===

    private CourseCreateCommand createValidCommand() {
        return new CourseCreateCommand(
                "테스트 강좌",
                "테스트 설명",
                "instructor-uuid",
                "https://thumbnail.url",
                Level.JUNIOR,
                List.of(1L,2L,3L),
                List.of(createSectionCommand("섹션1"), createSectionCommand("섹션2"))
        );
    }

    private CourseCreateCommand createCommandWithSectionsAndLectures() {
        return new CourseCreateCommand(
                "instructor-uuid",
                "https://thumbnail.url",
                "테스트 강좌",
                "테스트 설명",
                Level.JUNIOR,
                List.of(1L, 2L),
                List.of(
                        createSectionCommandWithLectures("섹션1", 3),
                        createSectionCommandWithLectures("섹션2", 2)
                )
        );
    }

    private CourseCreateCommand createCommandWithoutSections() {
        return new CourseCreateCommand(
                "테스트 강좌",
                "테스트 설명",
                "instructor-uuid",
                "https://thumbnail.url",
                Level.JUNIOR,
                List.of(1L, 2L),
                List.of()
        );
    }

    private CourseCreateCommand.SectionCreateCommand createSectionCommand(String title) {
        return new CourseCreateCommand.SectionCreateCommand(
                title,
                List.of(createLectureCommand("강의1"))
        );
    }

    private CourseCreateCommand.SectionCreateCommand createSectionCommandWithLectures(String title, int lectureCount) {
        List<LectureCreateCommand> lectures = java.util.stream.IntStream.rangeClosed(1, lectureCount)
                .mapToObj(i -> createLectureCommand("강의" + i))
                .toList();

        return new CourseCreateCommand.SectionCreateCommand(title, lectures);
    }

    private LectureCreateCommand createLectureCommand(String title) {
        return new LectureCreateCommand(title, "https://video.url");
    }

    private InstructorResult createActiveInstructor() {
        return new InstructorResult(
                "instructor-uuid",
                "test",
                "INSTRUCTOR",
                "ACTIVE"
        );
    }
}
