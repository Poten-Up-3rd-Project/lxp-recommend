package com.lxp.auth.infrastructure.persistence.local.adapter;

import com.lxp.auth.domain.common.model.vo.UserId;
import com.lxp.auth.domain.local.model.entity.LocalAuth;
import com.lxp.auth.domain.local.model.vo.HashedPassword;
import com.lxp.auth.infrastructure.persistence.local.entity.JpaLocalAuth;
import org.springframework.stereotype.Component;

@Component
public class LocalAuthDomainMapper {

    public LocalAuth toDomain(JpaLocalAuth jpaLocalAuth) {
        return LocalAuth.of(
            UserId.of(jpaLocalAuth.getId()),
            jpaLocalAuth.getLoginIdentifier(),
            new HashedPassword(jpaLocalAuth.getHashedPassword())
        );
    }

    public JpaLocalAuth toEntity(LocalAuth localAuth) {
        return JpaLocalAuth.of(
            localAuth.userId().value(),
            localAuth.loginIdentifier(),
            localAuth.hashedPasswordAsString()
        );
    }
}
