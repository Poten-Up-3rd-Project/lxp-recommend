//package com.lxp.auth.application.local.service;
//
//import com.lxp.auth.application.local.handler.UserSavePortHandler;
//import com.lxp.auth.application.local.port.required.command.HandleUserRegisterCommand;
//import com.lxp.auth.application.local.port.required.usecase.RegisterUserUseCase;
//import com.lxp.auth.domain.local.model.entity.LocalAuth;
//import com.lxp.auth.domain.local.model.vo.HashedPassword;
//import com.lxp.auth.domain.local.policy.PasswordPolicy;
//import com.lxp.auth.domain.local.repository.LocalAuthRepository;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//@Slf4j
//@Service
//public class LocalRegisterService implements RegisterUserUseCase {
//
//    private final LocalAuthRepository localAuthRepository;
//    private final PasswordPolicy passwordPolicy;
//    private final UserSavePortHandler userSavePortHandler;
//
//    public LocalRegisterService(LocalAuthRepository localAuthRepository,
//                                PasswordPolicy passwordPolicy,
//                                UserSavePortHandler userSavePortHandler) {
//        this.localAuthRepository = localAuthRepository;
//        this.passwordPolicy = passwordPolicy;
//        this.userSavePortHandler = userSavePortHandler;
//    }
//
//    @Override
//    public Void execute(HandleUserRegisterCommand command) {
//        log.info("회원가입 로직 시작");
//
//        HashedPassword hashedPassword = passwordPolicy.apply(command.password());
//        LocalAuth register = LocalAuth.register(command.email(), hashedPassword);
//        localAuthRepository.save(register);
//
//        userSavePortHandler.handleUserRegister(register.userId(), command);
//        return null;
//    }
//
//}
