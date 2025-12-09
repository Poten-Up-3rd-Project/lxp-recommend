package com.lxp.auth.domain.common.model.vo;

import java.util.List;

public record TokenClaims(
    String userId,
    String email,
    List<String> authorities
) {
}
