package com.lxp.recommend.application;

import com.lxp.recommend.application.dto.CourseSyncCommand;
import com.lxp.recommend.application.port.in.EventIdempotencyUseCase;
import com.lxp.recommend.application.port.out.CourseRepository;
import com.lxp.recommend.application.port.out.UserRepository;
import com.lxp.recommend.application.service.CourseSyncService;
import com.lxp.recommend.domain.course.entity.RecommendCourse;
import com.lxp.recommend.domain.user.entity.Level;
import com.lxp.recommend.domain.user.entity.RecommendUser;
import com.lxp.recommend.domain.user.entity.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class CourseSyncUseCaseTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventIdempotencyUseCase eventIdempotencyUseCase;

    @InjectMocks
    private CourseSyncService courseSyncService;

    @Test
    @DisplayName("강의를 생성할 수 있다")
    void createCourse_success() {
        CourseSyncCommand command = new CourseSyncCommand(
                "evt-1",
                "course-1",
                List.of(1L, 2L),
                "MIDDLE",
                "instructor-1"
        );

        given(eventIdempotencyUseCase.isDuplicate("evt-1")).willReturn(false);
        given(courseRepository.existsById("course-1")).willReturn(false);
        given(courseRepository.save(any(RecommendCourse.class))).willAnswer(inv -> inv.getArgument(0));
        given(userRepository.findById("instructor-1")).willReturn(Optional.empty());

        courseSyncService.createCourse(command);

        ArgumentCaptor<RecommendCourse> captor = ArgumentCaptor.forClass(RecommendCourse.class);
        then(courseRepository).should().save(captor.capture());

        RecommendCourse saved = captor.getValue();
        assertThat(saved.getId()).isEqualTo("course-1");
        assertThat(saved.getTags()).containsExactly(1L, 2L);
        assertThat(saved.getLevel()).isEqualTo(Level.MIDDLE);
        assertThat(saved.getInstructorId()).isEqualTo("instructor-1");

        then(eventIdempotencyUseCase).should().markAsProcessed("evt-1");
    }

    @Test
    @DisplayName("강의 생성 시 강사의 created_course_ids가 업데이트된다")
    void createCourse_updatesInstructorCreatedCourses() {
        CourseSyncCommand command = new CourseSyncCommand(
                "evt-1",
                "course-1",
                List.of(1L, 2L),
                "MIDDLE",
                "instructor-1"
        );

        RecommendUser instructor = RecommendUser.builder()
                .id("instructor-1")
                .level(Level.EXPERT)
                .createdCourseIds(new ArrayList<>())
                .build();

        given(eventIdempotencyUseCase.isDuplicate("evt-1")).willReturn(false);
        given(courseRepository.existsById("course-1")).willReturn(false);
        given(courseRepository.save(any(RecommendCourse.class))).willAnswer(inv -> inv.getArgument(0));
        given(userRepository.findById("instructor-1")).willReturn(Optional.of(instructor));
        given(userRepository.save(any(RecommendUser.class))).willAnswer(inv -> inv.getArgument(0));

        courseSyncService.createCourse(command);

        assertThat(instructor.getCreatedCourseIds()).contains("course-1");
    }

    @Test
    @DisplayName("이미 존재하는 강의는 생성을 건너뛴다")
    void createCourse_alreadyExists() {
        CourseSyncCommand command = new CourseSyncCommand(
                "evt-1",
                "course-1",
                List.of(1L, 2L),
                "MIDDLE",
                "instructor-1"
        );

        given(eventIdempotencyUseCase.isDuplicate("evt-1")).willReturn(false);
        given(courseRepository.existsById("course-1")).willReturn(true);

        courseSyncService.createCourse(command);

        then(courseRepository).should(never()).save(any());
    }

    @Test
    @DisplayName("중복 이벤트는 처리하지 않는다")
    void createCourse_duplicateEvent() {
        CourseSyncCommand command = new CourseSyncCommand(
                "evt-1",
                "course-1",
                List.of(1L, 2L),
                "MIDDLE",
                "instructor-1"
        );

        given(eventIdempotencyUseCase.isDuplicate("evt-1")).willReturn(true);

        courseSyncService.createCourse(command);

        then(courseRepository).should(never()).save(any());
        then(eventIdempotencyUseCase).should(never()).markAsProcessed(any());
    }

    @Test
    @DisplayName("강의를 삭제(비활성화)할 수 있다")
    void deleteCourse_success() {
        RecommendCourse course = RecommendCourse.builder()
                .id("course-1")
                .level(Level.MIDDLE)
                .instructorId("instructor-1")
                .status(Status.ACTIVE)
                .build();

        given(eventIdempotencyUseCase.isDuplicate("evt-2")).willReturn(false);
        given(courseRepository.findById("course-1")).willReturn(Optional.of(course));
        given(courseRepository.save(any(RecommendCourse.class))).willAnswer(inv -> inv.getArgument(0));
        given(userRepository.findById("instructor-1")).willReturn(Optional.empty());

        courseSyncService.deleteCourse("evt-2", "course-1");

        assertThat(course.getStatus()).isEqualTo(Status.INACTIVE);
        then(eventIdempotencyUseCase).should().markAsProcessed("evt-2");
    }

    @Test
    @DisplayName("강의 삭제 시 강사의 created_course_ids에서 제거된다")
    void deleteCourse_removesFromInstructorCreatedCourses() {
        RecommendCourse course = RecommendCourse.builder()
                .id("course-1")
                .level(Level.MIDDLE)
                .instructorId("instructor-1")
                .status(Status.ACTIVE)
                .build();

        RecommendUser instructor = RecommendUser.builder()
                .id("instructor-1")
                .level(Level.EXPERT)
                .createdCourseIds(new ArrayList<>(List.of("course-1", "course-2")))
                .build();

        given(eventIdempotencyUseCase.isDuplicate("evt-2")).willReturn(false);
        given(courseRepository.findById("course-1")).willReturn(Optional.of(course));
        given(courseRepository.save(any(RecommendCourse.class))).willAnswer(inv -> inv.getArgument(0));
        given(userRepository.findById("instructor-1")).willReturn(Optional.of(instructor));
        given(userRepository.save(any(RecommendUser.class))).willAnswer(inv -> inv.getArgument(0));

        courseSyncService.deleteCourse("evt-2", "course-1");

        assertThat(instructor.getCreatedCourseIds()).containsExactly("course-2");
    }

    @Test
    @DisplayName("존재하지 않는 강의 삭제는 건너뛴다")
    void deleteCourse_notFound() {
        given(eventIdempotencyUseCase.isDuplicate("evt-2")).willReturn(false);
        given(courseRepository.findById("course-1")).willReturn(Optional.empty());

        courseSyncService.deleteCourse("evt-2", "course-1");

        then(courseRepository).should(never()).save(any());
        then(eventIdempotencyUseCase).should().markAsProcessed("evt-2");
    }
}
