package com.lxp.auth.application.local.port.provided.policy;

import com.lxp.auth.domain.common.model.vo.TokenClaims;

public interface AuthenticationConverter {

    TokenClaims convertToClaims(String userId, String email, String role);

}
