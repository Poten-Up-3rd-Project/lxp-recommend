package com.lxp.auth.infrastructure.persistence.local.repository;

import com.lxp.auth.infrastructure.persistence.local.entity.JpaLocalAuth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaLocalAuthRepository extends JpaRepository<JpaLocalAuth, UUID> {

    Optional<JpaLocalAuth> findByLoginIdentifier(String loginIdentifier);

}
