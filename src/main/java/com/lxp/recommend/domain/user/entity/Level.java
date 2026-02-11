package com.lxp.recommend.domain.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Level {
    JUNIOR(1),
    MIDDLE(2),
    SENIOR(3),
    EXPERT(4);

    private final int value;

    public static Level fromValue(int value) {
        for (Level level : values()) {
            if (level.value == value) {
                return level;
            }
        }
        throw new IllegalArgumentException("Unknown level value: " + value);
    }

    public static Level fromString(String name) {
        for (Level level : values()) {
            if (level.name().equalsIgnoreCase(name)) {
                return level;
            }
        }
        throw new IllegalArgumentException("Unknown level: " + name);
    }
}
