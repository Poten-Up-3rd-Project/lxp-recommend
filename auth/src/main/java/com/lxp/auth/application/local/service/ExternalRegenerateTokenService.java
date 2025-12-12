package com.lxp.auth.application.local.service;

import com.lxp.api.auth.port.dto.command.RegenerateTokenCommand;
import com.lxp.api.auth.port.dto.result.AuthenticationResponse;
import com.lxp.api.auth.port.external.ExternalRegenerateTokenPort;
import com.lxp.auth.application.local.port.required.adapter.AuthUserMapper;
import com.lxp.auth.application.local.port.required.dto.AuthTokenInfo;
import com.lxp.auth.domain.common.policy.JwtPolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExternalRegenerateTokenService implements ExternalRegenerateTokenPort {

    private final JwtPolicy jwtTokenProvider;
    private final AuthUserMapper authUserMapper;

    @Override
    public Optional<AuthenticationResponse> regenerateToken(RegenerateTokenCommand command) {
        AuthTokenInfo token = jwtTokenProvider.createToken(authUserMapper.to(command));
        return Optional.of(authUserMapper.to(token));
    }
}
