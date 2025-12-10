package com.lxp.auth.application.local.port.required.usecase;

import com.lxp.auth.application.local.port.required.command.HandleLogoutCommand;
import com.lxp.common.application.cqrs.CommandWithResultHandler;
import com.lxp.common.application.port.in.CommandWithResultUseCase;

@FunctionalInterface
public interface LogoutUserUseCase extends CommandWithResultUseCase<HandleLogoutCommand, Void> {
}
