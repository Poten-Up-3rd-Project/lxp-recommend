package com.lxp.tag.infrastructure.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lxp.api.tag.port.external.TagCachePort;
import com.lxp.api.tag.port.dto.result.TagResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class TagCacheAdapter implements TagCachePort {

    private static final String TAG_ID_KEY = "tag:id:";
    private static final String TAG_NAME_KEY = "tag:name:";
    private static final String TAG_ALL_KEY = "tags:all";
    private static final long TTL_HOURS = 24;

    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public List<TagResult> findAll() {
        Set<String> keys = redisTemplate.keys(TAG_ID_KEY + "*");

        if (keys == null || keys.isEmpty()) {
            return List.of();
        }

        List<String> cached = redisTemplate.opsForValue().multiGet(keys);

        if (cached == null) {
            return List.of();
        }

        return cached.stream()
                .filter(Objects::nonNull)
                .map(this::toTagResult)
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public Optional<TagResult> findById(Long id) {
        String key = TAG_ID_KEY + id;
        String json = redisTemplate.opsForValue().get(key);

        if (json == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(objectMapper.readValue(json, TagResult.class));
        } catch (JsonProcessingException e) {
            log.error("[TagCache] 역직렬화 실패 - id={}", id, e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Long> findByName(String name) {
        String key = TAG_NAME_KEY + name.toLowerCase();
        String value = redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(value).map(Long::parseLong);
    }

    @Override
    public List<TagResult> findByIds(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }

        List<String> keys = ids.stream()
                .map(id -> TAG_ID_KEY + id)  // 수정: TAG_KEY_PREFIX → TAG_ID_KEY
                .toList();

        List<String> cached = redisTemplate.opsForValue().multiGet(keys);  // 수정: List<Object> → List<String>

        if (cached == null) {
            return List.of();
        }

        return cached.stream()
                .filter(Objects::nonNull)
                .map(this::toTagResult)       // 수정: JSON 변환 추가
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public void save(TagResult tag) {
        try {
            String json = objectMapper.writeValueAsString(tag);

            redisTemplate.opsForValue().set(
                    TAG_ID_KEY + tag.tagId(),
                    json,
                    TTL_HOURS,
                    TimeUnit.HOURS
            );

            redisTemplate.opsForValue().set(
                    TAG_NAME_KEY + tag.name().toLowerCase(),
                    String.valueOf(tag.tagId()),
                    TTL_HOURS,
                    TimeUnit.HOURS
            );

            log.debug("[TagCache] 저장 완료 - id={}, name={}", tag.tagId(), tag.name());
        } catch (JsonProcessingException e) {
            log.error("[TagCache] 직렬화 실패 - id={}", tag.tagId(), e);
        }
    }

    @Override
    public void refreshAll(List<TagResult> tags) {
        log.info("[TagCache] 전체 캐시 갱신 시작 - count={}", tags.size());

        evictAll();

        for (TagResult tag : tags) {
            save(tag);
        }

        log.info("[TagCache] 전체 캐시 갱신 완료");
    }

    @Override
    public void evictAll() {
        Set<String> idKeys = redisTemplate.keys(TAG_ID_KEY + "*");
        Set<String> nameKeys = redisTemplate.keys(TAG_NAME_KEY + "*");

        if (idKeys != null && !idKeys.isEmpty()) {
            redisTemplate.delete(idKeys);
        }
        if (nameKeys != null && !nameKeys.isEmpty()) {
            redisTemplate.delete(nameKeys);
        }

        log.info("[TagCache] 전체 캐시 삭제 완료");
    }

    private TagResult toTagResult(String json) {
        try {
            return objectMapper.readValue(json, TagResult.class);
        } catch (JsonProcessingException e) {
            log.error("[TagCache] 역직렬화 실패 - json={}", json, e);
            return null;
        }
    }
}