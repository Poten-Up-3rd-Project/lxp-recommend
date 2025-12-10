package com.lxp.user.presentation.rest;

import com.lxp.common.annotation.CurrentUserId;
import com.lxp.common.infrastructure.exception.ApiResponse;
import com.lxp.user.application.port.required.command.ExecuteSearchUserCommand;
import com.lxp.user.application.port.required.command.ExecuteUpdateUserCommand;
import com.lxp.user.application.port.required.command.ExecuteWithdrawUserCommand;
import com.lxp.user.application.port.required.dto.UserInfoDto;
import com.lxp.user.application.port.required.usecase.SearchUserProfileUseCase;
import com.lxp.user.application.port.required.usecase.UpdateUserProfileUseCase;
import com.lxp.user.application.port.required.usecase.WithdrawUserUseCase;
import com.lxp.user.presentation.rest.dto.request.UserUpdateRequest;
import com.lxp.user.presentation.rest.dto.response.UserInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api-v1/users")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_LEARNER') || hasAuthority('ROLE_INSTRUCTOR')")
public class UserController {

    private final SearchUserProfileUseCase searchUserProfileUseCase;
    private final UpdateUserProfileUseCase updateUserProfileUseCase;
    private final WithdrawUserUseCase withdrawUserUseCase;

    @GetMapping
    public ApiResponse<UserInfoResponse> getUserInfo(@CurrentUserId String userId) {
        UserInfoDto userInfoDto = searchUserProfileUseCase.execute(new ExecuteSearchUserCommand(userId));
        return ApiResponse.success(UserInfoResponse.to(userInfoDto));
    }

    @PatchMapping
    public ApiResponse<UserInfoResponse> updateUserInfo(@CurrentUserId String userId,
                                                        @RequestBody UserUpdateRequest request) {
        UserInfoDto userInfoDto = updateUserProfileUseCase.execute(new ExecuteUpdateUserCommand(userId, request.name(), request.level(), request.tagIds(), request.job()));
        return ApiResponse.success(UserInfoResponse.to(userInfoDto));
    }

    @DeleteMapping
    public ApiResponse<Void> deleteUserInfo(@CurrentUserId String userId) {
        withdrawUserUseCase.execute(new ExecuteWithdrawUserCommand(userId));
        return ApiResponse.success();
    }

}
