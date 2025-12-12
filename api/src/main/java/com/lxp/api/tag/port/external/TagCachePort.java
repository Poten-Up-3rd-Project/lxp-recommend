package com.lxp.api.tag.port.external;

import com.lxp.api.tag.port.dto.result.TagResult;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

// TODO(추후 별도 서비스로 빼기)
public interface TagCachePort {

    /**
     * 전체 태그 목록 조회
     */
    List<TagResult> findAll();

    /**
     * ID로 태그 조회
     */
    Optional<TagResult> findById(Long id);

    /**
     * 이름으로 태그 조회
     */
    Optional<Long> findByName(String name);

    /**
     * 여러 ID로 태그 조회
     */
    List<TagResult> findByIds(Collection<Long> ids);

    /**
     * 태그 저장 (캐시 갱신)
     */
    void save(TagResult tag);

    /**
     * 전체 태그 캐시 갱신
     */
    void refreshAll(List<TagResult> tags);

    /**
     * 캐시 무효화
     */
    void evictAll();
}