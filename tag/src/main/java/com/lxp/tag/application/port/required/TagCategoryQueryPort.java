package com.lxp.tag.application.port.required;

import com.lxp.tag.application.port.query.CategoryResult;

import java.util.List;

public interface TagCategoryQueryPort {
    List<CategoryResult> findAllWithTags();
}
