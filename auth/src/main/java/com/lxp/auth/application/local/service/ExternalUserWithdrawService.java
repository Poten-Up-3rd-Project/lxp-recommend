package com.lxp.auth.application.local.service;

import com.lxp.api.auth.port.dto.command.UserWithdrawCommand;
import com.lxp.api.auth.port.external.ExternalUserWithdrawPort;
import com.lxp.auth.domain.common.policy.JwtPolicy;
import com.lxp.auth.domain.common.policy.TokenRevocationPolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExternalUserWithdrawService implements ExternalUserWithdrawPort {

    private final JwtPolicy jwtPolicy;
    private final TokenRevocationPolicy tokenRevocationPolicy;

    @Override
    public void invalidate(UserWithdrawCommand command) {
        long expirationTimeMillis = jwtPolicy.getExpirationTimeMillis(command.accessToken());
        long remainingSeconds = 0;
        long nowMillis = System.currentTimeMillis();

        if (expirationTimeMillis > nowMillis) {
            remainingSeconds = (expirationTimeMillis - nowMillis) / 1000;
        }

        if (remainingSeconds > 0) {
            tokenRevocationPolicy.revokeAccessToken(command.accessToken(), remainingSeconds);
        }
    }
}
