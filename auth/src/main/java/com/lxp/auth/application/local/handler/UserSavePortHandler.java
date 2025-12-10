//package com.lxp.auth.application.local.handler;
//
//import com.lxp.auth.application.local.port.required.command.HandleUserRegisterCommand;
//import com.lxp.auth.domain.common.model.vo.UserId;
//import com.lxp.user.application.command.UserSaveCommand;
//import com.lxp.user.application.port.api.external.ExternalUserSavePort;
//import org.springframework.stereotype.Component;
//
//@Component
//public class UserSavePortHandler {
//
//    private final ExternalUserSavePort externalUserSavePort;
//
//    public UserSavePortHandler(ExternalUserSavePort externalUserSavePort) {
//        this.externalUserSavePort = externalUserSavePort;
//    }
//
//    public void handleUserRegister(UserId userId, HandleUserRegisterCommand command) {
//        externalUserSavePort.saveUser(toSaveCommand(userId, command));
//    }
//
//    private UserSaveCommand toSaveCommand(UserId userId, HandleUserRegisterCommand command) {
//        return new UserSaveCommand(
//            userId.value(),
//            command.name(),
//            command.email(),
//            command.role(),
//            command.tags(),
//            command.learnerLevel(),
//            command.job()
//        );
//    }
//}
