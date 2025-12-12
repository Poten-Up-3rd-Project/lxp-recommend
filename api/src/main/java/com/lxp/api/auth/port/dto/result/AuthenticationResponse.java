package com.lxp.api.auth.port.dto.result;

public record AuthenticationResponse(String accessToken, long expiresIn) {
}
