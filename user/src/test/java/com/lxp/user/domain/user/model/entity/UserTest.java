package com.lxp.user.domain.user.model.entity;

import com.lxp.user.domain.common.model.vo.UserId;
import com.lxp.user.domain.profile.model.entity.UserProfile;
import com.lxp.user.domain.profile.model.vo.LearnerLevel;
import com.lxp.user.domain.profile.model.vo.Tags;
import com.lxp.user.domain.user.model.vo.UserEmail;
import com.lxp.user.domain.user.model.vo.UserName;
import com.lxp.user.domain.user.model.vo.UserRole;
import com.lxp.user.domain.user.model.vo.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserTest {
    private UserId userId;
    private UserName initialName;
    private UserEmail userEmail;
    private UserProfile initialProfile;
    private LearnerLevel initialLevel;
    private Tags initialTags;
    private String initialJob;

    @BeforeEach
    void setUp() {
        userId = UserId.create();
        initialName = new UserName("test");
        userEmail = new UserEmail("initial@example.com");

        // 💡 UserProfile 초기화에 필요한 데이터 설정
        initialLevel = LearnerLevel.MIDDLE;
        initialTags = new Tags(List.of(1L, 2L, 3L));
        initialJob = "Engineer";

        // 💡 UserProfile 객체 생성
        initialProfile = UserProfile.create(userId, initialLevel, initialTags, initialJob);
    }

    // --- 1. 생성자 및 팩토리 메서드 테스트 ---

    @Test
    @DisplayName("Learner 팩토리 메서드로 User 객체를 생성하고 초기 상태를 확인한다")
    void createLearner_success() {
        // when
        User learner = User.createLearner(userId, initialName, userEmail, initialProfile);

        // then
        assertAll(
            () -> assertNotNull(learner, "User 객체는 null이 아니어야 한다."),
            () -> assertEquals(userId, learner.id(), "ID가 일치해야 한다."),
            () -> assertEquals(UserRole.LEARNER, learner.role(), "역할은 LEARNER여야 한다."),
            () -> assertEquals(UserStatus.ACTIVE, learner.userStatus(), "상태는 ACTIVE여야 한다."),
            () -> assertNotNull(learner.profile(), "UserProfile이 설정되어야 한다."),
            () -> assertEquals(initialLevel, learner.profile().level(), "UserProfile의 레벨이 일치해야 한다.")
        );
    }

    @Test
    @DisplayName("Instructor 팩토리 메서드로 User 객체를 생성하고 초기 상태를 확인한다")
    void createInstructor_success() {
        // when
        User instructor = User.createInstructor(userId, initialName, userEmail, initialProfile);

        // then
        assertAll(
            () -> assertEquals(UserRole.INSTRUCTOR, instructor.role(), "역할은 INSTRUCTOR여야 한다."),
            () -> assertEquals(UserStatus.ACTIVE, instructor.userStatus(), "상태는 ACTIVE여야 한다."),
            () -> assertNotNull(instructor.profile(), "UserProfile이 설정되어야 한다.")
        );
    }

    @Test
    @DisplayName("Admin 팩토리 메서드로 User 객체를 생성하고 초기 상태를 확인한다")
    void createAdmin_success() {
        // Admin은 UserProfile을 null로 받음
        // when
        User admin = User.createAdmin(userId, initialName, userEmail);

        // then
        assertAll(
            () -> assertEquals(UserRole.ADMIN, admin.role(), "역할은 ADMIN여야 한다."),
            () -> assertEquals(UserStatus.ACTIVE, admin.userStatus(), "상태는 ACTIVE여야 한다."),
            () -> assertNull(admin.profile(), "Admin은 UserProfile이 null이어야 한다.")
        );
    }

    @Test
    @DisplayName("정적 of 메서드로 User 객체를 생성하고 초기 상태를 확인한다")
    void createOf_success() {
        // given
        UserRole testRole = UserRole.INSTRUCTOR;
        UserStatus testUserStatus = UserStatus.ACTIVE;
        LocalDateTime testDeletedAt = null;

        // when
        User user = User.of(userId, initialName, userEmail, testRole, testUserStatus, initialProfile, testDeletedAt);

        // then
        assertAll(
            () -> assertEquals(testRole, user.role(), "전달된 역할과 일치해야 한다."),
            () -> assertEquals(testUserStatus, user.userStatus(), "상태가 일치해야 한다."),
            () -> assertNotNull(user.profile(), "UserProfile이 설정되어야 한다.")
        );
    }

    // --- 2. 상태 변경 메서드 테스트 ---

    @Test
    @DisplayName("update: 사용자 이름 및 프로필을 성공적으로 업데이트한다")
    void update_success() {
        // given
        User learner = User.createLearner(userId, initialName, userEmail, initialProfile);
        UserName newName = new UserName("new");
        LearnerLevel newLevel = LearnerLevel.EXPERT;
        List<Long> newTags = List.of(99L, 100L, 33L);
        String newJob = "CTO";

        // when
        learner.update(newName, newLevel, newTags, newJob);

        // then
        assertAll(
            () -> assertEquals(newName.value(), learner.name(), "이름이 새 이름으로 업데이트되어야 한다."),
            () -> assertEquals(newLevel, learner.profile().level(), "LearnerLevel이 업데이트되어야 한다."),
            () -> assertEquals(new Tags(newTags).values(), learner.profile().tags().values(), "Tags가 업데이트되어야 한다."),
            () -> assertEquals(newJob, learner.profile().job(), "Job이 업데이트되어야 한다.")
        );
    }

    @Test
    @DisplayName("update: DELETED 상태인 User는 업데이트되지 않는다")
    void update_deletedUserDoesNotUpdate() {
        // given
        User learner = User.createLearner(userId, initialName, userEmail, initialProfile);
        learner.withdraw(); // DELETED 상태로 변경

        UserName newName = new UserName("new");
        LearnerLevel newLevel = LearnerLevel.EXPERT;

        // when
        learner.update(newName, newLevel, List.of(), "CTO");

        // then
        assertAll(
            () -> assertEquals(initialName.value(), learner.name(), "이름이 변경되지 않아야 한다."),
            () -> assertEquals(initialLevel, learner.profile().level(), "레벨이 변경되지 않아야 한다."),
            () -> assertEquals(UserStatus.DELETED, learner.userStatus(), "상태는 DELETED로 유지되어야 한다.")
        );
    }


    @Test
    @DisplayName("makeInstructor: ACTIVE인 LEARNER는 INSTRUCTOR로 승급된다")
    void makeInstructor_fromLearnerToInstructor() {
        // given
        User learner = User.createLearner(userId, initialName, userEmail, initialProfile);
        assertEquals(UserRole.LEARNER, learner.role());

        // when
        learner.makeInstructor();

        // then
        assertEquals(UserRole.INSTRUCTOR, learner.role(), "역할이 INSTRUCTOR로 변경되어야 한다.");
    }

    @Test
    @DisplayName("makeInstructor: 이미 INSTRUCTOR인 경우 역할은 변하지 않는다")
    void makeInstructor_alreadyInstructor() {
        // given
        User instructor = User.createInstructor(userId, initialName, userEmail, initialProfile);
        assertEquals(UserRole.INSTRUCTOR, instructor.role());

        // when
        instructor.makeInstructor();

        // then
        assertEquals(UserRole.INSTRUCTOR, instructor.role(), "역할은 여전히 INSTRUCTOR여야 한다.");
    }

    @Test
    @DisplayName("makeInstructor: DELETED 상태인 LEARNER는 INSTRUCTOR로 승급되지 않는다")
    void makeInstructor_deletedUserCannotBePromoted() {
        // given
        User learner = User.createLearner(userId, initialName, userEmail, initialProfile);
        learner.withdraw(); // DELETED 상태로 변경
        assertEquals(UserStatus.DELETED, learner.userStatus());

        // when
        learner.makeInstructor();

        // then
        assertEquals(UserRole.LEARNER, learner.role(), "DELETED 상태이므로 역할은 LEARNER로 유지되어야 한다.");
    }

    @Test
    @DisplayName("withdraw: 사용자 상태를 DELETED로 변경하고 deletedAt을 설정한다")
    void withdraw_success() {
        // Given
        User learner = User.createLearner(userId, initialName, userEmail, initialProfile);
        assertEquals(UserStatus.ACTIVE, learner.userStatus());
        assertNull(learner.deletedAt(), "초기에는 deletedAt이 null이어야 한다.");

        // When
        learner.withdraw();

        // Then
        assertAll(
            () -> assertEquals(UserStatus.DELETED, learner.userStatus(), "사용자 상태가 DELETED로 변경되어야 한다."),
            () -> assertNotNull(learner.deletedAt(), "deletedAt이 설정되어야 한다."),
            () -> assertTrue(learner.deletedAt().isBefore(LocalDateTime.now().plusSeconds(1)), "deletedAt은 현재 시간과 거의 일치해야 한다.")
        );
    }

    // --- 3. 권한 확인 메서드 테스트 ---

    @Test
    @DisplayName("canManageOwnCourse: INSTRUCTOR는 자신의 코스를 관리할 수 있다")
    void canManageOwnCourse_instructor() {
        // given
        User instructor = User.createInstructor(userId, initialName, userEmail, initialProfile);

        // then
        assertTrue(instructor.canManageOwnCourse(), "INSTRUCTOR는 코스를 관리할 수 있어야 한다.");
    }

    @Test
    @DisplayName("canManageOwnCourse: ADMIN은 자신의 코스를 관리할 수 있다")
    void canManageOwnCourse_admin() {
        // given
        User admin = User.createAdmin(userId, initialName, userEmail);

        // then
        assertTrue(admin.canManageOwnCourse(), "ADMIN은 코스를 관리할 수 있어야 한다.");
    }

    @Test
    @DisplayName("canManageOwnCourse: LEARNER는 자신의 코스를 관리할 수 없다")
    void canManageOwnCourse_learner() {
        // given
        User learner = User.createLearner(userId, initialName, userEmail, initialProfile);

        // then
        assertFalse(learner.canManageOwnCourse(), "LEARNER는 코스를 관리할 수 없어야 한다.");
    }

    @Test
    @DisplayName("canManageAllCourses: ADMIN은 모든 코스를 관리할 수 있다")
    void canManageAllCourses_admin() {
        // given
        User admin = User.createAdmin(userId, initialName, userEmail);

        // then
        assertTrue(admin.canManageAllCourses(), "ADMIN은 모든 코스를 관리할 수 있어야 한다.");
    }

    @Test
    @DisplayName("canManageAllCourses: INSTRUCTOR는 모든 코스를 관리할 수 없다")
    void canManageAllCourses_instructor() {
        // given
        User instructor = User.createInstructor(userId, initialName, userEmail, initialProfile);

        // then
        assertFalse(instructor.canManageAllCourses(), "INSTRUCTOR는 모든 코스를 관리할 수 없어야 한다.");
    }

    @Test
    @DisplayName("canManageAllCourses: LEARNER는 모든 코스를 관리할 수 없다")
    void canManageAllCourses_learner() {
        // given
        User learner = User.createLearner(userId, initialName, userEmail, initialProfile);

        // then
        assertFalse(learner.canManageAllCourses(), "LEARNER는 모든 코스를 관리할 수 없어야 한다.");
    }

}
