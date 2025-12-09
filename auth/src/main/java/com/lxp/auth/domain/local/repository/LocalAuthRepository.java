package com.lxp.auth.domain.local.repository;

import com.lxp.auth.domain.local.model.entity.LocalAuth;

import java.util.Optional;
import java.util.UUID;

public interface LocalAuthRepository {

    Optional<LocalAuth> findByLoginIdentifier(String loginIdentifier);

    Optional<LocalAuth> findByUserId(UUID userId);

    void save(LocalAuth localAuth);

}
