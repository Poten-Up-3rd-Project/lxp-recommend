package com.lxp.user.application.port.required.adapter;

import com.lxp.api.auth.port.dto.command.UserWithdrawCommand;
import com.lxp.api.auth.port.external.ExternalUserWithdrawPort;
import com.lxp.common.application.cqrs.CommandWithResultHandler;
import com.lxp.user.application.port.required.command.ExecuteWithdrawUserCommand;
import org.springframework.stereotype.Component;

@Component
public class UserWithdrawHandler implements CommandWithResultHandler<ExecuteWithdrawUserCommand, Void> {

    private final ExternalUserWithdrawPort externalUserWithdrawPort;

    public UserWithdrawHandler(ExternalUserWithdrawPort externalUserWithdrawPort) {
        this.externalUserWithdrawPort = externalUserWithdrawPort;
    }

    @Override
    public Void handle(ExecuteWithdrawUserCommand command) {
        externalUserWithdrawPort.invalidate(new UserWithdrawCommand(command.cookie()));
        return null;
    }
}
