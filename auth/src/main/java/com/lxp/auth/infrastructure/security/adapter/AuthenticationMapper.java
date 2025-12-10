package com.lxp.auth.infrastructure.security.adapter;

import com.lxp.auth.application.local.port.provided.policy.AuthenticationConverter;
import com.lxp.auth.domain.common.model.vo.TokenClaims;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AuthenticationMapper implements AuthenticationConverter {

    private static final String ROLE = "ROLE_";

    @Override
    public TokenClaims convertToClaims(String userId, String email, String role) {
        return new TokenClaims(
            userId,
            email,
            List.of(setAuthority(role))
        );
    }

    private String setAuthority(String authority) {
        return authority.startsWith(ROLE) ? authority : ROLE + authority;
    }
}
