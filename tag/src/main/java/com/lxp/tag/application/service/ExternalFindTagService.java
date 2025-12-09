package com.lxp.tag.application.service;

import com.lxp.tag.application.port.provided.external.ExternalFindTagPort;
import com.lxp.tag.application.port.query.TagResult;
import com.lxp.tag.infrastructure.external.TagQueryAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExternalFindTagService implements ExternalFindTagPort {
    private final TagQueryAdapter tagQueryAdapter;

    @Override
    public List<TagResult> findByIds(List<Long> ids) {
        return tagQueryAdapter.findByIds(ids);
    }

    @Override
    public Optional<TagResult> findById(Long id) {
        return tagQueryAdapter.findById(id);
    }

    @Override
    public Optional<TagResult> findIdByName(String name) {
        return tagQueryAdapter.findIdByName(name);
    }
}
