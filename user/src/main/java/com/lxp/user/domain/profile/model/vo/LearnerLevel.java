package com.lxp.user.domain.profile.model.vo;

import java.util.Arrays;
import java.util.Optional;

public enum LearnerLevel {

    JUNIOR("주니어"),
    MIDDLE("미들"),
    SENIOR("시니어"),
    EXPERT("익스퍼트");

    private final String description;

    LearnerLevel(String description) {
        this.description = description;
    }

    public static Optional<LearnerLevel> fromString(String levelName) {
        return Arrays.stream(LearnerLevel.values())
            .filter(role -> role.name().equalsIgnoreCase(levelName))
            .findFirst();
    }
}
