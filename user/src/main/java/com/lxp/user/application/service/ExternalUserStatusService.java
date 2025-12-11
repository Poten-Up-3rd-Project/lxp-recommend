package com.lxp.user.application.service;

import com.lxp.api.user.port.external.ExternalUserStatusPort;
import com.lxp.user.domain.common.model.vo.UserId;
import com.lxp.user.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExternalUserStatusService implements ExternalUserStatusPort {

    private final UserRepository userRepository;

    @Override
    public Optional<String> getStatusByUserId(String userId) {
        return userRepository.findUserStatusById(UserId.of(userId)).map(Enum::name);
    }
}
