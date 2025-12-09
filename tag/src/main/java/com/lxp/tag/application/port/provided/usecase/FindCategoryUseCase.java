package com.lxp.tag.application.port.provided.usecase;

import com.lxp.tag.application.port.query.CategoryResult;

import java.util.List;

public interface FindCategoryUseCase {
    List<CategoryResult> findAll();
}
