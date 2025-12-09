package com.lxp.auth.application.local.port.required.command;

import com.lxp.common.application.cqrs.Command;

public record HandleLoginCommand(String email, String password) implements Command {
}
