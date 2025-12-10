//package com.lxp.auth.application.local.handler;
//
//import com.lxp.auth.application.local.port.provided.command.HandleUserSearchCommand;
//import com.lxp.auth.application.local.port.required.command.HandleUserInfoCommand;
//import com.lxp.common.application.cqrs.CommandWithResultHandler;
//import com.lxp.user.application.port.api.dto.UserAuthResponse;
//import com.lxp.user.application.port.api.external.ExternalUserAuthPort;
//import org.springframework.stereotype.Component;
//
//@Component
//public class UserInfoSearchPortHandler implements CommandWithResultHandler<HandleUserSearchCommand, HandleUserInfoCommand> {
//
//    private final ExternalUserAuthPort externalUserAuthPort;
//
//    public UserInfoSearchPortHandler(ExternalUserAuthPort externalUserAuthPort) {
//        this.externalUserAuthPort = externalUserAuthPort;
//    }
//
//    @Override
//    public HandleUserInfoCommand handle(HandleUserSearchCommand command) {
//        UserAuthResponse userAuthResponse = externalUserAuthPort.userAuth(command.userId()).get();
//        return new HandleUserInfoCommand(userAuthResponse.userId(), userAuthResponse.email(), userAuthResponse.role());
//    }
//
//    public HandleUserInfoCommand getUserInfoByEmail(String email) {
//        UserAuthResponse userInfoByEmail = externalUserAuthPort.getUserInfoByEmail(email).get();
//        return new HandleUserInfoCommand(userInfoByEmail.userId(), userInfoByEmail.email(), userInfoByEmail.role());
//    }
//}
