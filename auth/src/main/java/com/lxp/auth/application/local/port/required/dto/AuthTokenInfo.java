package com.lxp.auth.application.local.port.required.dto;

public record AuthTokenInfo(String accessToken, long expiresIn) {
}
