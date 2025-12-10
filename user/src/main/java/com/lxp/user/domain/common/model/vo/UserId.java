package com.lxp.user.domain.common.model.vo;

import java.util.Objects;
import java.util.UUID;

public record UserId(UUID value) {

    public UserId {
        Objects.requireNonNull(value, "userId는 null일 수 없습니다.");
    }

    public static UserId create() {
        return new UserId(UUID.randomUUID());
    }

    public static UserId of(UUID value) {
        return new UserId(value);
    }

    public static UserId of(String value) {
        return new UserId(UUID.fromString(value));
    }

    public String asString() {
        return value.toString();
    }

}
