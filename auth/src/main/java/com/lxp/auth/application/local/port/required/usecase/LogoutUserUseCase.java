package com.lxp.auth.application.local.port.required.usecase;

import com.lxp.auth.application.local.port.required.command.HandleLogoutCommand;
import com.lxp.common.application.cqrs.CommandWithResultHandler;

@FunctionalInterface
public interface LogoutUserUseCase extends CommandWithResultHandler<HandleLogoutCommand, Void> {
}
