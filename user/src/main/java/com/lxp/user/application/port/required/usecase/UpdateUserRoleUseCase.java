package com.lxp.user.application.port.required.usecase;

import com.lxp.api.auth.port.dto.result.AuthenticationResponse;
import com.lxp.common.application.port.in.CommandWithResultUseCase;
import com.lxp.user.application.port.required.command.ExecuteUpdateRoleUserCommand;

@FunctionalInterface
public interface UpdateUserRoleUseCase extends CommandWithResultUseCase<String, AuthenticationResponse> {
}
