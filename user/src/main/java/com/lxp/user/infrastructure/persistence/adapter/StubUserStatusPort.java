//package com.lxp.user.infrastructure.persistence.adapter;
//
//import com.lxp.user.api.external.ExternalUserStatusPort;
//import com.lxp.user.domain.common.model.vo.UserId;
//import com.lxp.user.domain.user.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//
//import java.util.Optional;
//
//@Component
//@RequiredArgsConstructor
//public class StubUserStatusPort implements ExternalUserStatusPort {
//
//    private final UserRepository userRepository;
//
//    @Override
//    public Optional<String> getStatusByUserId(String userId) {
//        return userRepository.findUserStatusById(UserId.of(userId)).map(Enum::name);
//    }
//}
