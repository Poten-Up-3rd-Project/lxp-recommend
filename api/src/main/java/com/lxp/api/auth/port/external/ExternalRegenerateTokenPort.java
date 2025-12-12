package com.lxp.api.auth.port.external;

import com.lxp.api.auth.port.dto.command.RegenerateTokenCommand;
import com.lxp.api.auth.port.dto.result.AuthenticationResponse;

import java.util.Optional;

@FunctionalInterface
public interface ExternalRegenerateTokenPort {

    Optional<AuthenticationResponse> regenerateToken(RegenerateTokenCommand command);

}
