package com.lxp.user.application.port.required.adapter;

import com.lxp.api.auth.port.dto.command.RegenerateTokenCommand;
import com.lxp.api.auth.port.dto.result.AuthenticationResponse;
import com.lxp.api.auth.port.external.ExternalRegenerateTokenPort;
import com.lxp.common.application.cqrs.CommandWithResultHandler;
import com.lxp.user.application.port.required.command.ExecuteUpdateRoleUserCommand;
import com.lxp.user.domain.common.exception.UserNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class RegenerateTokenPortHandler implements CommandWithResultHandler<ExecuteUpdateRoleUserCommand, AuthenticationResponse> {

    private final ExternalRegenerateTokenPort externalRegenerateTokenPort;

    public RegenerateTokenPortHandler(ExternalRegenerateTokenPort externalRegenerateTokenPort) {
        this.externalRegenerateTokenPort = externalRegenerateTokenPort;
    }

    @Override
    public AuthenticationResponse handle(ExecuteUpdateRoleUserCommand command) {
        return externalRegenerateTokenPort.regenerateToken(new RegenerateTokenCommand(
            command.userId(), command.email(), command.role()
        )).orElseThrow(UserNotFoundException::new);
    }
}
