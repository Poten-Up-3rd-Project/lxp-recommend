package com.lxp.recommend.application.port.out;

import com.lxp.recommend.domain.user.entity.RecommendUser;
import com.lxp.recommend.domain.user.entity.Status;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    RecommendUser save(RecommendUser user);

    Optional<RecommendUser> findById(String id);

    List<RecommendUser> findByStatus(Status status);

    long countByStatus(Status status);

    boolean existsById(String id);
}
