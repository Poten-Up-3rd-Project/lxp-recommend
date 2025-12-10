package com.lxp.auth.application.local.port.provided.command;

import com.lxp.common.application.cqrs.Command;

public record HandleUserSearchCommand(String userId) implements Command {
}
