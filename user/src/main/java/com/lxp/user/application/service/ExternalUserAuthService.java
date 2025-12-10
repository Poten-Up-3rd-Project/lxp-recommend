//package com.lxp.user.application.service;
//
//import com.lxp.user.api.dto.UserAuthResponse;
//import com.lxp.user.api.external.ExternalUserAuthPort;
//import com.lxp.user.domain.common.model.vo.UserId;
//import com.lxp.user.domain.user.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.Optional;
//
//@Service
//@RequiredArgsConstructor
//public class ExternalUserAuthService implements ExternalUserAuthPort {
//
//    private final UserRepository userRepository;
//
//    @Override
//    public Optional<UserAuthResponse> userAuth(String userId) {
//        return userRepository.findUserById(UserId.of(userId)).map(UserAuthResponse::of);
//    }
//
//    @Override
//    public Optional<UserAuthResponse> getUserInfoByEmail(String email) {
//        return userRepository.findUserByEmail(email).map(UserAuthResponse::of);
//    }
//}
