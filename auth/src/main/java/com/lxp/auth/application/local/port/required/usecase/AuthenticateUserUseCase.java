package com.lxp.auth.application.local.port.required.usecase;

import com.lxp.auth.application.local.port.required.command.HandleLoginCommand;
import com.lxp.auth.application.local.port.required.dto.AuthTokenInfo;
import com.lxp.common.application.cqrs.CommandWithResultHandler;

@FunctionalInterface
public interface AuthenticateUserUseCase extends CommandWithResultHandler<HandleLoginCommand, AuthTokenInfo> {
}
