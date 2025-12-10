package com.lxp.user.application.port.required.usecase;

import com.lxp.common.application.port.in.CommandWithResultUseCase;
import com.lxp.user.application.port.required.command.ExecuteSearchUserCommand;
import com.lxp.user.application.port.required.dto.UserInfoDto;

@FunctionalInterface
public interface SearchUserProfileUseCase extends CommandWithResultUseCase<ExecuteSearchUserCommand, UserInfoDto> {
}
