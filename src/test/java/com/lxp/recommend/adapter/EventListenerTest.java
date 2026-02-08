package com.lxp.recommend.adapter;

import com.lxp.recommend.adapter.in.event.CourseEventListener;
import com.lxp.recommend.adapter.in.event.EnrollEventListener;
import com.lxp.recommend.adapter.in.event.UserEventListener;
import com.lxp.recommend.application.dto.CourseSyncCommand;
import com.lxp.recommend.application.dto.EnrollSyncCommand;
import com.lxp.recommend.application.dto.UserSyncCommand;
import com.lxp.recommend.application.port.in.CourseSyncUseCase;
import com.lxp.recommend.application.port.in.EnrollSyncUseCase;
import com.lxp.recommend.application.port.in.UserSyncUseCase;
import com.lxp.recommend.dto.event.CourseEventPayload;
import com.lxp.recommend.dto.event.EnrollEventPayload;
import com.lxp.recommend.dto.event.UserEventPayload;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class EventListenerTest {

    @Mock
    private UserSyncUseCase userSyncUseCase;

    @Mock
    private CourseSyncUseCase courseSyncUseCase;

    @Mock
    private EnrollSyncUseCase enrollSyncUseCase;

    @InjectMocks
    private UserEventListener userEventListener;

    @InjectMocks
    private CourseEventListener courseEventListener;

    @InjectMocks
    private EnrollEventListener enrollEventListener;

    @Test
    @DisplayName("USER_CREATED 이벤트를 처리한다")
    void handleUserCreated() {
        UserEventPayload payload = new UserEventPayload(
                "USER_CREATED",
                "user-1",
                List.of(1L, 2L),
                "JUNIOR",
                LocalDateTime.now()
        );

        willDoNothing().given(userSyncUseCase).createUser(any(UserSyncCommand.class));

        userEventListener.handle(payload);

        then(userSyncUseCase).should().createUser(any(UserSyncCommand.class));
    }

    @Test
    @DisplayName("USER_UPDATED 이벤트를 처리한다")
    void handleUserUpdated() {
        UserEventPayload payload = new UserEventPayload(
                "USER_UPDATED",
                "user-1",
                List.of(3L, 4L),
                "MIDDLE",
                LocalDateTime.now()
        );

        willDoNothing().given(userSyncUseCase).updateUser(any(UserSyncCommand.class));

        userEventListener.handle(payload);

        then(userSyncUseCase).should().updateUser(any(UserSyncCommand.class));
    }

    @Test
    @DisplayName("USER_DELETED 이벤트를 처리한다")
    void handleUserDeleted() {
        UserEventPayload payload = new UserEventPayload(
                "USER_DELETED",
                "user-1",
                null,
                null,
                LocalDateTime.now()
        );

        willDoNothing().given(userSyncUseCase).deleteUser("user-1");

        userEventListener.handle(payload);

        then(userSyncUseCase).should().deleteUser("user-1");
    }

    @Test
    @DisplayName("COURSE_CREATED 이벤트를 처리한다")
    void handleCourseCreated() {
        CourseEventPayload payload = new CourseEventPayload(
                "COURSE_CREATED",
                "course-1",
                List.of(1L, 2L),
                "MIDDLE",
                "instructor-1",
                LocalDateTime.now()
        );

        willDoNothing().given(courseSyncUseCase).createCourse(any(CourseSyncCommand.class));

        courseEventListener.handle(payload);

        then(courseSyncUseCase).should().createCourse(any(CourseSyncCommand.class));
    }

    @Test
    @DisplayName("COURSE_DELETED 이벤트를 처리한다")
    void handleCourseDeleted() {
        CourseEventPayload payload = new CourseEventPayload(
                "COURSE_DELETED",
                "course-1",
                null,
                null,
                null,
                LocalDateTime.now()
        );

        willDoNothing().given(courseSyncUseCase).deleteCourse("course-1");

        courseEventListener.handle(payload);

        then(courseSyncUseCase).should().deleteCourse("course-1");
    }

    @Test
    @DisplayName("ENROLL_CREATED 이벤트를 처리한다")
    void handleEnrollCreated() {
        EnrollEventPayload payload = new EnrollEventPayload(
                "ENROLL_CREATED",
                "user-1",
                "course-1",
                LocalDateTime.now()
        );

        willDoNothing().given(enrollSyncUseCase).createEnrollment(any(EnrollSyncCommand.class));

        enrollEventListener.handle(payload);

        then(enrollSyncUseCase).should().createEnrollment(any(EnrollSyncCommand.class));
    }

    @Test
    @DisplayName("ENROLL_DELETED 이벤트를 처리한다")
    void handleEnrollDeleted() {
        EnrollEventPayload payload = new EnrollEventPayload(
                "ENROLL_DELETED",
                "user-1",
                "course-1",
                LocalDateTime.now()
        );

        willDoNothing().given(enrollSyncUseCase).deleteEnrollment(any(EnrollSyncCommand.class));

        enrollEventListener.handle(payload);

        then(enrollSyncUseCase).should().deleteEnrollment(any(EnrollSyncCommand.class));
    }
}
