package com.lxp.user.application.port.api.dto;

import com.lxp.user.domain.user.model.entity.User;

/**
 * auth에서 로그인 시 jwt 토큰에 role을 저장하기 위한 용도로 구현
 *
 * @param userId
 * @param email
 * @param role
 */
public record UserAuthResponse(String userId, String email, String role) {

    public static UserAuthResponse of(User user) {
        return new UserAuthResponse(
            user.id().asString(),
            user.email(),
            user.role().name()
        );
    }

}
