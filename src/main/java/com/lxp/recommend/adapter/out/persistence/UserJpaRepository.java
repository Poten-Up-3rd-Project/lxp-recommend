package com.lxp.recommend.adapter.out.persistence;

import com.lxp.recommend.domain.user.entity.RecommendUser;
import com.lxp.recommend.domain.user.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserJpaRepository extends JpaRepository<RecommendUser, String> {

    List<RecommendUser> findByStatus(Status status);

    long countByStatus(Status status);
}
