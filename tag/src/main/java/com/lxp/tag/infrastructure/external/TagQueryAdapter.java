package com.lxp.tag.infrastructure.external;

import com.lxp.tag.application.port.query.TagResult;
import com.lxp.tag.application.port.required.TagQueryPort;
import com.lxp.tag.infrastructure.persistence.jpa.TagJpaRepository;
import com.lxp.tag.infrastructure.persistence.jpa.entity.TagJpaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TagQueryAdapter implements TagQueryPort {
    private final TagJpaRepository tagJpaRepository;

    @Override
    public List<TagResult> findByIds(List<Long> ids) {
        return tagJpaRepository.findByIdIn(ids)
                .stream().map(TagJpaEntity::toResult)
                .toList();
    }

    @Override
    public Optional<TagResult> findById(Long id) {
        return tagJpaRepository.findById(id).map(TagJpaEntity::toResult);
    }

    @Override
    public Optional<TagResult> findIdByName(String name) {
        return tagJpaRepository.findByName(name).map(TagJpaEntity::toResult);
    }
}
