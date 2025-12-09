package com.lxp.auth.application.local.port.required.usecase;

import com.lxp.auth.application.local.port.required.command.HandleUserRegisterCommand;
import com.lxp.common.application.cqrs.CommandWithResultHandler;

@FunctionalInterface
public interface RegisterUserUseCase extends CommandWithResultHandler<HandleUserRegisterCommand, Void> {
}
