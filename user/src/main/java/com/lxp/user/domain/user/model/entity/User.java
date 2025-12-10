package com.lxp.user.domain.user.model.entity;

import com.lxp.common.domain.event.AggregateRoot;
import com.lxp.user.domain.common.model.vo.UserId;
import com.lxp.user.domain.profile.model.entity.UserProfile;
import com.lxp.user.domain.profile.model.vo.LearnerLevel;
import com.lxp.user.domain.user.model.vo.UserEmail;
import com.lxp.user.domain.user.model.vo.UserName;
import com.lxp.user.domain.user.model.vo.UserRole;
import com.lxp.user.domain.user.model.vo.UserStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class User extends AggregateRoot<UserId> {

    private UserId id;

    private UserName name;

    private UserEmail email;

    private UserRole role;

    private UserStatus userStatus;

    private UserProfile userProfile;

    private LocalDateTime deletedAt;

    private User(UserId id, UserName name, UserEmail email, UserRole userRole, UserStatus userStatus, UserProfile userProfile, LocalDateTime deletedAt) {
        this.id = Objects.requireNonNull(id, "userId는 null일 수 없습니다.");
        this.name = Objects.requireNonNull(name, "userName은 null일 수 없습니다.");
        this.email = Objects.requireNonNull(email, "userEmail은 null일 수 없습니다.");
        this.role = Objects.requireNonNull(userRole, "userRole은 null일 수 없습니다.");
        this.userStatus = userStatus;
        this.userProfile = userProfile;
        this.deletedAt = deletedAt;
    }

    public static User of(UserId id, UserName name, UserEmail email, UserRole userRole, UserStatus userStatus, UserProfile userProfile, LocalDateTime deletedAt) {
        return new User(id, name, email, userRole, userStatus, userProfile, deletedAt);
    }

    public static User createLearner(UserId id, UserName name, UserEmail email, UserProfile userProfile) {
        return new User(id, name, email, UserRole.LEARNER, UserStatus.ACTIVE, userProfile, null);
    }

    public static User createInstructor(UserId id, UserName name, UserEmail email, UserProfile userProfile) {
        return new User(id, name, email, UserRole.INSTRUCTOR, UserStatus.ACTIVE, userProfile, null);
    }

    public static User createAdmin(UserId id, UserName name, UserEmail email) {
        return new User(id, name, email, UserRole.ADMIN, UserStatus.ACTIVE, null, null);
    }

    public void update(UserName name, LearnerLevel level, List<Long> tags, String job) {
        if (!isActive()) {
            return;
        }

        this.name = name == null ? this.name : name;
        this.userProfile.update(level, tags, job);
    }

    public void makeInstructor() {
        if (this.role == UserRole.LEARNER && this.userStatus == UserStatus.ACTIVE) {
            this.role = UserRole.INSTRUCTOR;
        }
    }

    public boolean canManageOwnCourse() {
        return this.role == UserRole.INSTRUCTOR || this.role == UserRole.ADMIN;
    }

    public boolean canManageAllCourses() {
        return this.role == UserRole.ADMIN;
    }

    public boolean isActive() {
        return this.userStatus == UserStatus.ACTIVE;
    }

    public void withdraw() {
        this.userStatus = UserStatus.DELETED;
        this.deletedAt = LocalDateTime.now();
    }

    public UserId id() {
        return this.id;
    }

    public String name() {
        return this.name.value();
    }

    public String email() {
        return this.email.value();
    }

    public UserRole role() {
        return this.role;
    }

    public UserStatus userStatus() {
        return this.userStatus;
    }

    public UserProfile profile() {
        return this.userProfile;
    }

    public LocalDateTime deletedAt() {
        return this.deletedAt;
    }

    @Override
    public UserId getId() {
        return this.id;
    }

}
