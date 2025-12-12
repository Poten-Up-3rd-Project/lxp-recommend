package com.lxp.user.application.service;

import com.lxp.api.user.port.dto.result.UserAuthResponse;
import com.lxp.api.user.port.external.ExternalUserAuthPort;
import com.lxp.user.application.port.required.adapter.UserAuthMapper;
import com.lxp.user.domain.common.model.vo.UserId;
import com.lxp.user.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExternalUserAuthService implements ExternalUserAuthPort {

    private final UserRepository userRepository;
    private final UserAuthMapper userAuthMapper;

    @Override
    public Optional<UserAuthResponse> userAuth(String userId) {
        return userRepository.findUserById(UserId.of(userId)).map(userAuthMapper::toResponse);
    }

    @Override
    public Optional<UserAuthResponse> getUserInfoByEmail(String email) {
        return userRepository.findUserByEmail(email).map(userAuthMapper::toResponse);
    }
}
