package com.lxp.user.domain.user.model.vo;

import java.util.Arrays;
import java.util.Optional;

public enum UserStatus {
    ACTIVE,
    DELETED;

    public static Optional<UserStatus> fromString(String statusName) {
        return Arrays.stream(UserStatus.values())
            .filter(role -> role.name().equalsIgnoreCase(statusName))
            .findFirst();
    }

    public boolean catTransitionTo(UserStatus other) {
        return switch (this) {
            case ACTIVE -> other == ACTIVE || other == DELETED;
            case DELETED -> false;
        };
    }

    public boolean isActive() {
        return this == ACTIVE;
    }
}
