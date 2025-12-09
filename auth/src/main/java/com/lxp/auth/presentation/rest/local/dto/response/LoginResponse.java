package com.lxp.auth.presentation.rest.local.dto.response;

public record LoginResponse(String accessToken, long expiresIn) {
}
