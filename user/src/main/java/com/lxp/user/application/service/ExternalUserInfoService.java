package com.lxp.user.application.service;

import com.lxp.api.user.port.dto.result.UserInfoResponse;
import com.lxp.api.user.port.external.ExternalUserInfoPort;
import com.lxp.user.application.port.adapter.UserAuthMapper;
import com.lxp.user.domain.common.model.vo.UserId;
import com.lxp.user.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExternalUserInfoService implements ExternalUserInfoPort {

    private final UserRepository userRepository;
    private final UserAuthMapper userAuthMapper;

    @Override
    public Optional<UserInfoResponse> userInfo(String userId) {
        return userRepository.findAggregateUserById(UserId.of(userId))
            .map(userAuthMapper::toInfoResponse);
    }
}
