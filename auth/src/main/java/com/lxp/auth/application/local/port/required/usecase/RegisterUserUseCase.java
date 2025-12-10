package com.lxp.auth.application.local.port.required.usecase;

import com.lxp.auth.application.local.port.required.command.HandleUserRegisterCommand;
import com.lxp.common.application.cqrs.CommandWithResultHandler;
import com.lxp.common.application.port.in.CommandWithResultUseCase;

@FunctionalInterface
public interface RegisterUserUseCase extends CommandWithResultUseCase<HandleUserRegisterCommand, Void> {
}
