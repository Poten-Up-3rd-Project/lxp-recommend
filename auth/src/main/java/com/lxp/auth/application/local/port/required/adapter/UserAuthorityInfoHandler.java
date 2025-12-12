package com.lxp.auth.application.local.port.required.adapter;

import com.lxp.api.user.port.dto.result.UserAuthResponse;
import com.lxp.api.user.port.external.ExternalUserAuthPort;
import com.lxp.auth.application.local.port.required.command.HandleUserAuthorityCommand;
import com.lxp.auth.domain.common.exception.EmailNotFoundException;
import com.lxp.common.application.cqrs.CommandWithResultHandler;
import org.springframework.stereotype.Component;

@Component
public class UserAuthorityInfoHandler implements CommandWithResultHandler<HandleUserAuthorityCommand, UserAuthResponse> {

    private final ExternalUserAuthPort externalUserAuthPort;

    public UserAuthorityInfoHandler(ExternalUserAuthPort externalUserAuthPort) {
        this.externalUserAuthPort = externalUserAuthPort;
    }

    @Override
    public UserAuthResponse handle(HandleUserAuthorityCommand command) {
        return externalUserAuthPort.getUserInfoByEmail(command.email())
            .orElseThrow(EmailNotFoundException::new);
    }
}
