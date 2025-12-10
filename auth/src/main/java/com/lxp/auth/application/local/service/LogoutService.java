package com.lxp.auth.application.local.service;

import com.lxp.auth.application.local.port.required.command.HandleLogoutCommand;
import com.lxp.auth.application.local.port.required.usecase.LogoutUserUseCase;
import com.lxp.auth.domain.common.policy.TokenRevocationPolicy;
import org.springframework.stereotype.Component;

@Component
public class LogoutService implements LogoutUserUseCase {

    private final TokenRevocationPolicy tokenRevocationPolicy;

    public LogoutService(TokenRevocationPolicy tokenRevocationPolicy) {
        this.tokenRevocationPolicy = tokenRevocationPolicy;
    }

    @Override
    public Void execute(HandleLogoutCommand command) {
        long remainingSeconds = command.remainingSeconds();
        if (remainingSeconds > 0) {
            tokenRevocationPolicy.revokeAccessToken(command.accessToken(), remainingSeconds);
        }
        return null;
    }
}
