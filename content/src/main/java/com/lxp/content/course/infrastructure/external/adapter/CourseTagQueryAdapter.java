package com.lxp.content.course.infrastructure.external.adapter;

import com.lxp.api.tag.port.external.TagCachePort;
import com.lxp.content.course.application.port.required.TagQueryPort;
import com.lxp.content.course.application.port.required.dto.TagResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CourseTagQueryAdapter implements TagQueryPort {
    private final TagCachePort tagCachePort;

    @Override
    public Long findTagByName(String name) {
        return tagCachePort.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Tag not found: " + name));
    }

    @Override
    public List<TagResult> findTagByIds(List<Long> tagId) {
        return tagCachePort.findByIds(tagId)
                .stream().map(it ->
                        new TagResult(it.tagId(),it.name(),it.color(),it.variant())
                ).toList();
    }

}
