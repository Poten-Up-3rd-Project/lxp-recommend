package com.lxp.tag.infrastructure.persistence.jpa;

import com.lxp.tag.infrastructure.persistence.jpa.entity.TagCategoryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagCategoryJpaRepository extends JpaRepository<TagCategoryJpaEntity, Long> {
    @Query("""
        select distinct c
        from TagCategoryJpaEntity c
        left join fetch c.tags
    """)
    List<TagCategoryJpaEntity> findAllWithTags();
}
