package com.lxp.api.auth.port.dto.command;

public record RegenerateTokenCommand(String userId, String email, String role) {
}
