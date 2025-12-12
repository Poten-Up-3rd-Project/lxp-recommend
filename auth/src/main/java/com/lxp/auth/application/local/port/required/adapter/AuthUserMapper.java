package com.lxp.auth.application.local.port.required.adapter;

import com.lxp.api.auth.port.dto.command.RegenerateTokenCommand;
import com.lxp.api.auth.port.dto.result.AuthenticationResponse;
import com.lxp.auth.application.local.port.provided.policy.AuthenticationConverter;
import com.lxp.auth.application.local.port.required.dto.AuthTokenInfo;
import com.lxp.auth.domain.common.model.vo.TokenClaims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthUserMapper {

    private final AuthenticationConverter authenticationConverter;

    public TokenClaims to(RegenerateTokenCommand command) {
        return authenticationConverter.convertToClaims(command.userId(), command.email(), command.role());
    }

    public AuthenticationResponse to(AuthTokenInfo tokenInfo) {
        return new AuthenticationResponse(tokenInfo.accessToken(), tokenInfo.expiresIn());
    }

}
