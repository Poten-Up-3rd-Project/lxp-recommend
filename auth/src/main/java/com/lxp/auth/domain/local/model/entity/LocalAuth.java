package com.lxp.auth.domain.local.model.entity;

import com.lxp.auth.domain.common.model.vo.UserId;
import com.lxp.auth.domain.local.model.vo.HashedPassword;
import com.lxp.common.domain.event.AggregateRoot;

import java.time.OffsetDateTime;
import java.util.Objects;

public class LocalAuth extends AggregateRoot<UserId> {

    private UserId id;
    private String loginIdentifier;
    private HashedPassword hashedPassword;
    private OffsetDateTime createdAt;
    private OffsetDateTime lastPasswordModifiedAt;

    private LocalAuth(UserId id, String loginIdentifier, HashedPassword hashedPassword, OffsetDateTime createdAt, OffsetDateTime lastPasswordModifiedAt) {
        this.id = Objects.requireNonNull(id);
        this.loginIdentifier = Objects.requireNonNull(loginIdentifier);
        this.hashedPassword = Objects.requireNonNull(hashedPassword);
        this.createdAt = Objects.requireNonNull(createdAt);
        this.lastPasswordModifiedAt = lastPasswordModifiedAt;
    }

    public static LocalAuth register(String loginIdentifier, HashedPassword hashedPassword) {
        return new LocalAuth(UserId.create(), loginIdentifier, hashedPassword, OffsetDateTime.now(), null);
    }

    public static LocalAuth of(
        UserId id,
        String loginIdentifier,
        HashedPassword passwordHash,
        OffsetDateTime createdAt,
        OffsetDateTime lastPasswordModifiedAt
    ) {
        return new LocalAuth(id, loginIdentifier, passwordHash, createdAt, lastPasswordModifiedAt);
    }

    public void updatePassword(final HashedPassword newHashedPassword) {
        this.hashedPassword = Objects.requireNonNull(newHashedPassword, "비밀번호는 null일 수 없습니다.");
        lastPasswordModifiedAt = OffsetDateTime.now();
    }

    public boolean matchesId(UserId id) {
        return this.id.matches(id);
    }

    public boolean matchesId(String id) {
        return this.id.matches(id);
    }

    public UserId userId() {
        return this.id;
    }

    public String loginIdentifier() {
        return this.loginIdentifier;
    }

    public HashedPassword hashedPassword() {
        return this.hashedPassword;
    }

    public String hashedPasswordAsString() {
        return this.hashedPassword.value();
    }

    public OffsetDateTime createdAt() {
        return this.createdAt;
    }

    public OffsetDateTime lastPasswordModifiedAt() {
        return this.lastPasswordModifiedAt;
    }

    @Override
    public UserId getId() {
        return this.id;
    }

}
