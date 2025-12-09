package com.lxp.auth.application.local.port.required.command;

import com.lxp.common.application.cqrs.Command;

public record HandleUserInfoCommand(String userId, String email, String role) implements Command {
}
