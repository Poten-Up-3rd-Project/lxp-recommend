package com.lxp.auth.presentation.rest.local;

import com.lxp.auth.application.local.port.required.command.HandleLogoutCommand;
import com.lxp.auth.application.local.port.required.dto.AuthTokenInfo;
import com.lxp.auth.application.local.port.required.usecase.AuthenticateUserUseCase;
import com.lxp.auth.application.local.port.required.usecase.LogoutUserUseCase;
import com.lxp.auth.application.local.port.required.usecase.RegisterUserUseCase;
import com.lxp.auth.domain.common.policy.JwtPolicy;
import com.lxp.auth.infrastructure.security.adapter.AuthHeaderResolver;
import com.lxp.auth.presentation.rest.local.dto.reqeust.LoginRequest;
import com.lxp.auth.presentation.rest.local.dto.reqeust.RegisterRequest;
import com.lxp.common.constants.CookieConstants;
import com.lxp.common.infrastructure.exception.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api-v1/auth")
@RequiredArgsConstructor
public class LocalAuthController {

    private final AuthenticateUserUseCase authenticateUserUseCase;
    private final RegisterUserUseCase registerUserUseCase;
    private final LogoutUserUseCase logoutUserUseCase;
    private final AuthHeaderResolver authHeaderResolver;
    private final JwtPolicy jwtPolicy;

    @PostMapping("/login")
    public ApiResponse<Void> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        AuthTokenInfo tokenInfo = authenticateUserUseCase.execute(request.toCommand());

        ResponseCookie cookie = ResponseCookie.from(CookieConstants.ACCESS_TOKEN_NAME, tokenInfo.accessToken())
            .httpOnly(CookieConstants.HTTP_ONLY)
            .secure(true)
            .path(CookieConstants.DEFAULT_PATH)
            .maxAge(tokenInfo.expiresIn())
            .sameSite("Lax")
            .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        response.setStatus(HttpServletResponse.SC_OK);
        return ApiResponse.success();
    }

    @PostMapping("/register")
    public ApiResponse<Void> register(@RequestBody RegisterRequest request) {
        registerUserUseCase.execute(request.toCommand());
        return ApiResponse.success();
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        String token = authHeaderResolver.resolveToken(request);

        if (token == null) {
            return ApiResponse.success();
        }

        long expirationTimeMillis = jwtPolicy.getExpirationTimeMillis(token);
        long currentTimeMillis = System.currentTimeMillis();
        long remainingSeconds = (expirationTimeMillis - currentTimeMillis) / 1000;

        logoutUserUseCase.execute(new HandleLogoutCommand(token, remainingSeconds));
        deleteCookie(response);
        return ApiResponse.success();
    }

    private void deleteCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(CookieConstants.ACCESS_TOKEN_NAME, "") // 값은 비움
            .httpOnly(CookieConstants.HTTP_ONLY)
            .secure(true)
            .path(CookieConstants.DEFAULT_PATH)
            .maxAge(0)
            .sameSite("Lax")
            .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
