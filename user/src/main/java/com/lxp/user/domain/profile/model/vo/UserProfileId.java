package com.lxp.user.domain.profile.model.vo;

import java.util.Objects;

public record UserProfileId(Long id) {

    public UserProfileId {
        Objects.requireNonNull(id, "id is null");
    }

    public boolean matches(UserProfileId userProfileId) {
        return this.id.equals(userProfileId.id);
    }

    public boolean matches(Long userProfileId) {
        return this.id.equals(userProfileId);
    }
}
