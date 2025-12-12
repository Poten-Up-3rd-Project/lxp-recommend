package com.lxp.auth.application.local.port.required.adapter;

import com.lxp.api.user.port.dto.result.UserAuthResponse;
import com.lxp.api.user.port.external.ExternalUserAuthPort;
import com.lxp.auth.application.local.port.provided.command.HandleUserSearchCommand;
import com.lxp.auth.application.local.port.required.command.HandleUserInfoCommand;
import com.lxp.common.application.cqrs.CommandWithResultHandler;
import org.springframework.stereotype.Component;

@Component
public class UserInfoSearchPortHandler implements CommandWithResultHandler<HandleUserSearchCommand, HandleUserInfoCommand> {

    private final ExternalUserAuthPort externalUserAuthPort;

    public UserInfoSearchPortHandler(ExternalUserAuthPort externalUserAuthPort) {
        this.externalUserAuthPort = externalUserAuthPort;
    }

    @Override
    public HandleUserInfoCommand handle(HandleUserSearchCommand command) {
        UserAuthResponse userAuthResponse = externalUserAuthPort.userAuth(command.userId())
            .orElseThrow();
        return new HandleUserInfoCommand(userAuthResponse.userId(), userAuthResponse.email(), userAuthResponse.role());
    }

}
