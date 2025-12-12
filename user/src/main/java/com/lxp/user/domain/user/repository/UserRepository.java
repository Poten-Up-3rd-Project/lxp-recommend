package com.lxp.user.domain.user.repository;

import com.lxp.user.domain.common.model.vo.UserId;
import com.lxp.user.domain.user.model.entity.User;
import com.lxp.user.domain.user.model.vo.UserStatus;

import java.util.Optional;

public interface UserRepository {

    Optional<UserStatus> findUserStatusById(UserId userId);

    Optional<User> findUserById(UserId userId);

    Optional<User> findUserByEmail(String email);

    Optional<User> findAggregateUserById(UserId userId);

    Optional<User> findAggregateUserByEmail(String email);

    void save(User user);

    void saveWithProfile(User user);

    void deactivate(User user);

}
