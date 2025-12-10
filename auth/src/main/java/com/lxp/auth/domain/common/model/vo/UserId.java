package com.lxp.auth.domain.common.model.vo;

import java.util.Objects;
import java.util.UUID;

public record UserId(UUID value) {

    public UserId {
        Objects.requireNonNull(value, "userId의 value는 null일 수 없습니다.");
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

    public boolean matches(UUID id) {
        return this.value.equals(id);
    }

    public boolean matches(UserId userId) {
        return this.equals(userId);
    }

    public boolean matches(String userId) {
        return this.asString().equals(userId);
    }

    public String asString() {
        return this.value.toString();
    }

}
