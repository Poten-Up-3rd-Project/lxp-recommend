package com.lxp.user.domain.user.model.entity;

import com.lxp.user.domain.common.model.vo.UserId;
import com.lxp.user.domain.user.model.vo.UserEmail;
import com.lxp.user.domain.user.model.vo.UserName;
import com.lxp.user.domain.user.model.vo.UserRole;
import com.lxp.user.domain.user.model.vo.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserTest {
    private UserId userId;
    private UserName initialName;
    private UserEmail userEmail;

    @BeforeEach
    void setUp() {
        userId = UserId.create();
        initialName = new UserName("test");
        userEmail = new UserEmail("initial@example.com");
    }

    // --- 1. 생성자 및 팩토리 메서드 테스트 ---

    @Test
    @DisplayName("Learner 팩토리 메서드로 User 객체를 생성하고 초기 상태를 확인한다")
    void createLearner_success() {
        // when
        User learner = User.createLearner(userId, initialName, userEmail);

        // then
        assertAll(
            () -> assertNotNull(learner, "User 객체는 null이 아니어야 한다."),
            () -> assertEquals(userId, learner.id(), "ID가 일치해야 한다."),
            () -> assertEquals(initialName.value(), learner.name(), "이름이 일치해야 한다."),
            () -> assertEquals(userEmail.value(), learner.email(), "이메일이 일치해야 한다."),
            () -> assertEquals(UserRole.LEARNER, learner.role(), "역할은 LEARNER여야 한다."),
            () -> assertEquals(UserStatus.ACTIVE, learner.userStatus(), "상태는 ACTIVE여야 한다.")
        );
    }

    @Test
    @DisplayName("Instructor 팩토리 메서드로 User 객체를 생성하고 초기 상태를 확인한다")
    void createInstructor_success() {
        // when
        User instructor = User.createInstructor(userId, initialName, userEmail);

        // then
        assertAll(
            () -> assertEquals(UserRole.INSTRUCTOR, instructor.role(), "역할은 INSTRUCTOR여야 한다."),
            () -> assertEquals(UserStatus.ACTIVE, instructor.userStatus(), "상태는 ACTIVE여야 한다.")
        );
    }

    @Test
    @DisplayName("Admin 팩토리 메서드로 User 객체를 생성하고 초기 상태를 확인한다")
    void createAdmin_success() {
        // when
        User admin = User.createAdmin(userId, initialName, userEmail);

        // then
        assertAll(
            () -> assertEquals(UserRole.ADMIN, admin.role(), "역할은 ADMIN여야 한다."),
            () -> assertEquals(UserStatus.ACTIVE, admin.userStatus(), "상태는 ACTIVE여야 한다.")
        );
    }

    @Test
    @DisplayName("정적 of 메서드로 User 객체를 생성하고 초기 상태를 확인한다")
    void createOf_success() {
        // given
        UserRole testRole = UserRole.INSTRUCTOR;
        UserStatus testUserStatus = UserStatus.ACTIVE;

        // when
        User user = User.of(userId, initialName, userEmail, testRole, testUserStatus);

        // then
        assertAll(
            () -> assertEquals(testRole, user.role(), "전달된 역할과 일치해야 한다."),
            () -> assertEquals(UserStatus.ACTIVE, user.userStatus(), "상태는 ACTIVE여야 한다.")
        );
    }

    // --- 2. 상태 변경 메서드 테스트 ---

    @Test
    @DisplayName("updateName: 사용자 이름을 성공적으로 업데이트한다")
    void updateName_success() {
        User learner = User.createLearner(userId, initialName, userEmail);
        UserName newName = new UserName("test2");

        // when
        learner.updateName(newName);

        // then
        assertEquals(newName.value(), learner.name(), "이름이 새 이름으로 업데이트되어야 한다.");
    }

    @Test
    @DisplayName("makeInstructor: ACTIVE인 LEARNER는 INSTRUCTOR로 승급된다")
    void makeInstructor_fromLearnerToInstructor() {
        // given
        User learner = User.createLearner(userId, initialName, userEmail);
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
        User instructor = User.createInstructor(userId, initialName, userEmail);
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
        User learner = User.createLearner(userId, initialName, userEmail);
        learner.withdraw(); // DELETED 상태로 변경
        assertEquals(UserStatus.DELETED, learner.userStatus());

        // when
        learner.makeInstructor();

        // then
        assertEquals(UserRole.LEARNER, learner.role(), "DELETED 상태이므로 역할은 LEARNER로 유지되어야 한다.");
    }

    @Test
    @DisplayName("withdraw: 사용자 상태를 DELETED로 변경한다")
    void withdraw_success() {
        // Given
        User learner = User.createLearner(userId, initialName, userEmail);
        assertEquals(UserStatus.ACTIVE, learner.userStatus());

        // When
        learner.withdraw();

        // Then
        assertEquals(UserStatus.DELETED, learner.userStatus(), "사용자 상태가 DELETED로 변경되어야 한다.");
    }

    // --- 3. 권한 확인 메서드 테스트 ---

    @Test
    @DisplayName("canManageOwnCourse: INSTRUCTOR는 자신의 코스를 관리할 수 있다")
    void canManageOwnCourse_instructor() {
        // given
        User instructor = User.createInstructor(userId, initialName, userEmail);

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
        User learner = User.createLearner(userId, initialName, userEmail);

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
        User instructor = User.createInstructor(userId, initialName, userEmail);

        // then
        assertFalse(instructor.canManageAllCourses(), "INSTRUCTOR는 모든 코스를 관리할 수 없어야 한다.");
    }

    @Test
    @DisplayName("canManageAllCourses: LEARNER는 모든 코스를 관리할 수 없다")
    void canManageAllCourses_learner() {
        // given
        User learner = User.createLearner(userId, initialName, userEmail);

        // then
        assertFalse(learner.canManageAllCourses(), "LEARNER는 모든 코스를 관리할 수 없어야 한다.");
    }

}
