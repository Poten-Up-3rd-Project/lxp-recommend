package com.lxp.recommend.application.port.in;

import com.lxp.recommend.application.dto.UserSyncCommand;

public interface UserSyncUseCase {

    void createUser(UserSyncCommand command);

    void updateUser(UserSyncCommand command);

    void deleteUser(String userId);
}
