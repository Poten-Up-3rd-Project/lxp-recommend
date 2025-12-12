package com.lxp.common.enums;

import java.util.Arrays;
import java.util.Optional;

public enum Level {
    JUNIOR("주니어"),
    MIDDLE("미들"),
    SENIOR("시니어"),
    EXPERT("익스퍼트");

    private final String description;

    Level(String description) {
        this.description = description;
    }

    public static Optional<Level> fromString(String name) {
        return Arrays.stream(values())
            .filter(level -> name.equalsIgnoreCase(level.name()))
            .findFirst();
    }
}
