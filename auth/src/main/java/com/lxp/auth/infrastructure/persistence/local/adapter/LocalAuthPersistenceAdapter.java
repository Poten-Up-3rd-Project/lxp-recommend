package com.lxp.auth.infrastructure.persistence.local.adapter;

import com.lxp.auth.domain.local.model.entity.LocalAuth;
import com.lxp.auth.domain.local.repository.LocalAuthRepository;
import com.lxp.auth.infrastructure.persistence.local.repository.JpaLocalAuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class LocalAuthPersistenceAdapter implements LocalAuthRepository {

    private final JpaLocalAuthRepository jpaLocalAuthRepository;
    private final LocalAuthDomainMapper localAuthDomainMapper;

    @Override
    public Optional<LocalAuth> findByLoginIdentifier(String loginIdentifier) {
        return jpaLocalAuthRepository.findByLoginIdentifier(loginIdentifier).map(localAuthDomainMapper::toDomain);
    }

    @Override
    public Optional<LocalAuth> findByUserId(UUID userId) {
        return jpaLocalAuthRepository.findById(userId).map(localAuthDomainMapper::toDomain);
    }

    @Override
    public void save(LocalAuth localAuth) {
        jpaLocalAuthRepository.save(localAuthDomainMapper.toEntity(localAuth));
    }

}
