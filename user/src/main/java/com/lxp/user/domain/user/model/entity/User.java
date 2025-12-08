package com.lxp.user.domain.user.model.entity;

import com.lxp.common.domain.event.AggregateRoot;
import com.lxp.user.domain.common.model.vo.UserId;
import com.lxp.user.domain.user.model.vo.UserEmail;
import com.lxp.user.domain.user.model.vo.UserName;
import com.lxp.user.domain.user.model.vo.UserRole;
import com.lxp.user.domain.user.model.vo.UserStatus;

import java.util.Objects;

public class User extends AggregateRoot<UserId> {

    private UserId id;

    private UserName name;

    private UserEmail email;

    private UserRole role;

    private UserStatus userStatus;

    private User(UserId id, UserName name, UserEmail email, UserRole userRole, UserStatus userStatus) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
        this.email = Objects.requireNonNull(email);
        this.role = Objects.requireNonNull(userRole);
        this.userStatus = userStatus;
    }

    public static User of(UserId id, UserName name, UserEmail email, UserRole userRole, UserStatus userStatus) {
        return new User(id, name, email, userRole, userStatus);
    }

    public static User createLearner(UserId id, UserName name, UserEmail email) {
        return new User(id, name, email, UserRole.LEARNER, UserStatus.ACTIVE);
    }

    public static User createInstructor(UserId id, UserName name, UserEmail email) {
        return new User(id, name, email, UserRole.INSTRUCTOR, UserStatus.ACTIVE);
    }

    public static User createAdmin(UserId id, UserName name, UserEmail email) {
        return new User(id, name, email, UserRole.ADMIN, UserStatus.ACTIVE);
    }

    public void updateName(UserName name) {
        this.name = Objects.requireNonNull(name);
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

    public void withdraw() {
        this.userStatus = UserStatus.DELETED;
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

    @Override
    public UserId getId() {
        return this.id;
    }

}
