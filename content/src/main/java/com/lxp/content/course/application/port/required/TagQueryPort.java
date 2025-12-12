package com.lxp.content.course.application.port.required;

import com.lxp.content.course.application.port.required.dto.TagResult;

import java.util.List;

public interface TagQueryPort {
    Long findTagByName(String name);
    List<TagResult> findTagByIds(List<Long> tagId);
}



