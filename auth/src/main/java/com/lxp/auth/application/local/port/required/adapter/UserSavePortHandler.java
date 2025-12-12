package com.lxp.auth.application.local.port.required.adapter;

import com.lxp.api.user.port.dto.command.UserRegisterCommand;
import com.lxp.api.user.port.dto.command.UserSaveCommand;
import com.lxp.api.user.port.external.ExternalUserSavePort;
import com.lxp.common.application.cqrs.CommandWithResultHandler;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserSavePortHandler implements CommandWithResultHandler<UserRegisterCommand, Void> {

    private final ExternalUserSavePort externalUserSavePort;

    public UserSavePortHandler(ExternalUserSavePort externalUserSavePort) {
        this.externalUserSavePort = externalUserSavePort;
    }

    @Override
    public Void handle(UserRegisterCommand command) {
        externalUserSavePort.saveUser(toSaveCommand(command));
        return null;
    }

    private UserSaveCommand toSaveCommand(UserRegisterCommand command) {
        return new UserSaveCommand(
            UUID.fromString(command.userId()),
            command.name(),
            command.email(),
            command.role(),
            command.tags(),
            command.learnerLevel(),
            command.job()
        );
    }

}
