package com.lxp.user.presentation.rest;

import com.lxp.common.annotation.CurrentUserId;
import com.lxp.common.constants.CookieConstants;
import com.lxp.common.infrastructure.exception.ApiResponse;
import com.lxp.user.application.port.provided.command.ExecuteSearchUserCommand;
import com.lxp.user.application.port.provided.command.ExecuteUpdateUserCommand;
import com.lxp.user.application.port.provided.command.ExecuteWithdrawUserCommand;
import com.lxp.user.application.port.provided.command.UpdateUserRoleCommand;
import com.lxp.user.application.port.provided.dto.UserInfoDto;
import com.lxp.user.application.port.provided.usecase.SearchUserProfileUseCase;
import com.lxp.user.application.port.provided.usecase.UpdateUserProfileUseCase;
import com.lxp.user.application.port.provided.usecase.UpdateUserRoleUseCase;
import com.lxp.user.application.port.provided.usecase.WithdrawUserUseCase;
import com.lxp.user.application.port.required.dto.AuthTokenResult;
import com.lxp.user.presentation.rest.dto.request.UserUpdateRequest;
import com.lxp.user.presentation.rest.dto.response.UserProfileResponse;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@Slf4j
@RestController
@RequestMapping("/api-v1/users")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_LEARNER') || hasAuthority('ROLE_INSTRUCTOR')")
public class UserController {

    private final SearchUserProfileUseCase searchUserProfileUseCase;
    private final UpdateUserProfileUseCase updateUserProfileUseCase;
    private final UpdateUserRoleUseCase updateUserRoleUseCase;
    private final WithdrawUserUseCase withdrawUserUseCase;

    @GetMapping
    public ResponseEntity<ApiResponse<UserProfileResponse>> getUserInfo(
        @Parameter(hidden = true)
        @CurrentUserId String userId) {
        UserInfoDto userInfoDto = searchUserProfileUseCase.execute(new ExecuteSearchUserCommand(userId));
        return ResponseEntity.ok(ApiResponse.success(UserProfileResponse.to(userInfoDto)));
    }

    @PatchMapping
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateUserInfo(
        @Parameter(hidden = true)
        @CurrentUserId String userId,
        @RequestBody UserUpdateRequest request
    ) {
        UserInfoDto userInfoDto = updateUserProfileUseCase.execute(
            new ExecuteUpdateUserCommand(userId, request.name(), request.level(), request.tagIds(), request.job())
        );
        return ResponseEntity.ok(ApiResponse.success(UserProfileResponse.to(userInfoDto)));
    }

    @PreAuthorize("hasAuthority('ROLE_LEARNER')")
    @PutMapping("/role")
    public ResponseEntity<ApiResponse<Void>> updateUserToInstructor(
        @Parameter(hidden = true)
        @CurrentUserId String userId,
        HttpServletRequest request,
        HttpServletResponse response
    ) {
        String token = getCookie(request);
        AuthTokenResult execute = updateUserRoleUseCase.execute(new UpdateUserRoleCommand(userId, token));
        ResponseCookie cookie = ResponseCookie.from(CookieConstants.ACCESS_TOKEN_NAME, execute.accessToken())
            .httpOnly(CookieConstants.HTTP_ONLY)
            .secure(false)
            .path(CookieConstants.DEFAULT_PATH)
            .maxAge(execute.expiresIn())
            .sameSite("Lax")
            .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        response.setStatus(HttpServletResponse.SC_OK);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteUserInfo(@Parameter(hidden = true) @CurrentUserId String userId,
                                                            HttpServletRequest request,
                                                            HttpServletResponse response) {
        String cookie = getCookie(request);
        withdrawUserUseCase.execute(new ExecuteWithdrawUserCommand(userId, cookie));

        removeCookie(response);
        return ResponseEntity.ok(ApiResponse.success());
    }

    private void removeCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(CookieConstants.ACCESS_TOKEN_NAME, "")
            .httpOnly(CookieConstants.HTTP_ONLY)
            .secure(false)
            .path(CookieConstants.DEFAULT_PATH)
            .maxAge(0)
            .sameSite("Lax")
            .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }


    public String getCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            log.info("Cookies is null");
            return null;
        }

        return Arrays.stream(cookies)
            .filter(cookie -> CookieConstants.ACCESS_TOKEN_NAME.equals(cookie.getName()))
            .map(Cookie::getValue)
            .filter(StringUtils::hasText)
            .findFirst()
            .orElse(null);
    }
}
