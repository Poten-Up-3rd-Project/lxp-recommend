package com.lxp.user.application.port.required.usecase;

import com.lxp.common.application.port.in.CommandWithResultUseCase;
import com.lxp.user.application.port.required.command.ExecuteUpdateUserCommand;
import com.lxp.user.application.port.required.dto.UserInfoDto;

@FunctionalInterface
public interface UpdateUserProfileUseCase extends CommandWithResultUseCase<ExecuteUpdateUserCommand, UserInfoDto> {
}
