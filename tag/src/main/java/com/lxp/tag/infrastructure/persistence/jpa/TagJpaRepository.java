package com.lxp.tag.infrastructure.persistence.jpa;

import com.lxp.tag.infrastructure.persistence.jpa.entity.TagJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TagJpaRepository extends JpaRepository<TagJpaEntity, Long> {
    List<TagJpaEntity> findByIdIn(List<Long> ids);

    Optional<TagJpaEntity> findByName(String tagName);

    @Query("""
        select t.id
        from TagJpaEntity t
        where t.name = :name
    """)
    Optional<Long> findIdByName(@Param("name") String name);
}
