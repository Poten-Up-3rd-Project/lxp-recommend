package com.lxp.content.course.domain.model.enums;

public enum CourseDifficulty {
    JUNIOR("주니어"),
    MIDDLE("미들"),
    SENIOR("시니어"),
    EXPERT("익스퍼트");

    private final String description;

    CourseDifficulty(String description) {
        this.description = description;
    }
}
