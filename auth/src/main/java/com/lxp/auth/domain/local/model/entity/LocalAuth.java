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

    private LocalAuth(UserId id, String loginIdentifier, HashedPassword hashedPassword) {
        this.id = Objects.requireNonNull(id, "userId는 null일 수 없습니다.");
        this.loginIdentifier = Objects.requireNonNull(loginIdentifier, "loginIdentifier는 null일 수 없습니다.");
        this.hashedPassword = Objects.requireNonNull(hashedPassword, "hashedPassword는 null일 수 없습니다.");
    }

    public static LocalAuth register(String loginIdentifier, HashedPassword hashedPassword) {
        return new LocalAuth(UserId.create(), loginIdentifier, hashedPassword);
    }

    public static LocalAuth of(
        UserId id,
        String loginIdentifier,
        HashedPassword passwordHash
    ) {
        return new LocalAuth(id, loginIdentifier, passwordHash);
    }

    public void updatePassword(final HashedPassword newHashedPassword) {
        this.hashedPassword = Objects.requireNonNull(newHashedPassword, "비밀번호는 null일 수 없습니다.");
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

    @Override
    public UserId getId() {
        return this.id;
    }

}
