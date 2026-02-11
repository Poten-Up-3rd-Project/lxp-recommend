package com.lxp.recommend.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lxp.recommend.adapter.in.event.CourseEventListener;
import com.lxp.recommend.adapter.in.event.EnrollEventListener;
import com.lxp.recommend.adapter.in.event.UserEventListener;
import com.lxp.recommend.application.dto.CourseSyncCommand;
import com.lxp.recommend.application.dto.EnrollSyncCommand;
import com.lxp.recommend.application.dto.UserSyncCommand;
import com.lxp.recommend.application.port.in.CourseSyncUseCase;
import com.lxp.recommend.application.port.in.EnrollSyncUseCase;
import com.lxp.recommend.application.port.in.UserSyncUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class EventListenerTest {

    @Mock
    private UserSyncUseCase userSyncUseCase;

    @Mock
    private CourseSyncUseCase courseSyncUseCase;

    @Mock
    private EnrollSyncUseCase enrollSyncUseCase;

    private ObjectMapper objectMapper;

    private UserEventListener userEventListener;
    private CourseEventListener courseEventListener;
    private EnrollEventListener enrollEventListener;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        userEventListener = new UserEventListener(userSyncUseCase, objectMapper);
        courseEventListener = new CourseEventListener(courseSyncUseCase, objectMapper);
        enrollEventListener = new EnrollEventListener(enrollSyncUseCase, objectMapper);
    }

    // ============ User Events ============

    @Test
    @DisplayName("user.created 이벤트를 처리한다")
    void handleUserCreated() {
        String message = """
                {
                    "eventId": "evt-1",
                    "eventType": "user.created",
                    "payload": {
                        "userId": "user-1",
                        "tagIds": [1, 2],
                        "level": "JUNIOR"
                    }
                }
                """;

        userEventListener.handle(message);

        then(userSyncUseCase).should().createUser(any(UserSyncCommand.class));
    }

    @Test
    @DisplayName("user.updated 이벤트를 처리한다")
    void handleUserUpdated() {
        String message = """
                {
                    "eventId": "evt-2",
                    "eventType": "user.updated",
                    "payload": {
                        "userId": "user-1",
                        "tagIds": [3, 4],
                        "level": "MIDDLE"
                    }
                }
                """;

        userEventListener.handle(message);

        then(userSyncUseCase).should().updateUser(any(UserSyncCommand.class));
    }

    @Test
    @DisplayName("user.deleted 이벤트를 처리한다")
    void handleUserDeleted() {
        String message = """
                {
                    "eventId": "evt-3",
                    "eventType": "user.deleted",
                    "payload": {
                        "userId": "user-1"
                    }
                }
                """;

        userEventListener.handle(message);

        then(userSyncUseCase).should().deleteUser(eq("evt-3"), eq("user-1"));
    }

    // ============ Course Events ============

    @Test
    @DisplayName("course.created 이벤트를 처리한다")
    void handleCourseCreated() {
        String message = """
                {
                    "eventId": "evt-4",
                    "eventType": "course.created",
                    "payload": {
                        "courseUuid": "course-1",
                        "instructorUuid": "instructor-1",
                        "difficulty": "MIDDLE",
                        "tagIds": [1, 2]
                    }
                }
                """;

        courseEventListener.handle(message);

        then(courseSyncUseCase).should().createCourse(any(CourseSyncCommand.class));
    }

    @Test
    @DisplayName("course.deleted 이벤트를 처리한다")
    void handleCourseDeleted() {
        String message = """
                {
                    "eventId": "evt-5",
                    "eventType": "course.deleted",
                    "payload": {
                        "courseUuid": "course-1"
                    }
                }
                """;

        courseEventListener.handle(message);

        then(courseSyncUseCase).should().deleteCourse(eq("evt-5"), eq("course-1"));
    }

    // ============ Enroll Events ============

    @Test
    @DisplayName("enroll.created 이벤트를 처리한다")
    void handleEnrollCreated() {
        String message = """
                {
                    "eventId": "evt-6",
                    "eventType": "enroll.created",
                    "payload": {
                        "userId": "user-1",
                        "courseId": "course-1"
                    }
                }
                """;

        enrollEventListener.handle(message);

        then(enrollSyncUseCase).should().createEnrollment(any(EnrollSyncCommand.class));
    }

    @Test
    @DisplayName("enroll.deleted 이벤트를 처리한다")
    void handleEnrollDeleted() {
        String message = """
                {
                    "eventId": "evt-7",
                    "eventType": "enroll.deleted",
                    "payload": {
                        "userId": "user-1",
                        "courseId": "course-1"
                    }
                }
                """;

        enrollEventListener.handle(message);

        then(enrollSyncUseCase).should().deleteEnrollment(any(EnrollSyncCommand.class));
    }
}
