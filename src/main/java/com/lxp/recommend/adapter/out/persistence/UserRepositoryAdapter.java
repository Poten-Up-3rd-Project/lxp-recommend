package com.lxp.recommend.adapter.out.persistence;

import com.lxp.recommend.domain.user.entity.RecommendUser;
import com.lxp.recommend.domain.user.entity.Status;
import com.lxp.recommend.application.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public RecommendUser save(RecommendUser user) {
        return userJpaRepository.save(user);
    }

    @Override
    public Optional<RecommendUser> findById(String id) {
        return userJpaRepository.findById(id);
    }

    @Override
    public List<RecommendUser> findByStatus(Status status) {
        return userJpaRepository.findByStatus(status);
    }

    @Override
    public long countByStatus(Status status) {
        return userJpaRepository.countByStatus(status);
    }

    @Override
    public boolean existsById(String id) {
        return userJpaRepository.existsById(id);
    }
}
