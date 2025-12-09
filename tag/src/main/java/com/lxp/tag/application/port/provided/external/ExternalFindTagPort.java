package com.lxp.tag.application.port.provided.external;

import com.lxp.tag.application.port.query.TagResult;

import java.util.List;
import java.util.Optional;

public interface ExternalFindTagPort {
    List<TagResult> findByIds(List<Long> ids);
    Optional<TagResult> findById(Long id);
    Optional<TagResult> findIdByName(String name);
}
