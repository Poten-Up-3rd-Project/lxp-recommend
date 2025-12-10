package com.lxp.auth.infrastructure.security.adapter;

import com.lxp.auth.domain.common.policy.TokenRevocationPolicy;
import org.springframework.stereotype.Component;

@Component
public class RedisTokenRevocationAdapter implements TokenRevocationPolicy {

    @Override
    public void revokeAccessToken(String token, long durationSeconds) {

    }

    @Override
    public boolean isTokenBlacklisted(String token) {
        return false;
    }
}
