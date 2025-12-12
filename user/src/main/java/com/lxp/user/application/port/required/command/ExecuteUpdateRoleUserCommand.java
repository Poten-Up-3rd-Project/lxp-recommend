package com.lxp.user.application.port.required.command;

import com.lxp.common.application.cqrs.Command;

public record ExecuteUpdateRoleUserCommand(String userId, String email, String role) implements Command {
}
