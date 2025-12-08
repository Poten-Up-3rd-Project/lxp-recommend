package com.lxp.auth.domain.local.model.vo;

import java.util.Objects;

public record HashedPassword(String value) {

    public HashedPassword {
        if (Objects.isNull(value) || value.isBlank()) {
            throw new IllegalArgumentException("HashedPassword value cannot be empty.");
        }
    }

}
