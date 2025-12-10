package com.lxp.user.domain.user.model.vo;

import java.util.Arrays;
import java.util.Optional;

public enum UserRole {

    LEARNER("학습자"), INSTRUCTOR("강사"), ADMIN("관리자");

    private final String description;

    UserRole(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static Optional<UserRole> fromString(String roleName) {
        return Arrays.stream(values())
            .filter(role -> role.name().equalsIgnoreCase(roleName))
            .findFirst();
    }
}
