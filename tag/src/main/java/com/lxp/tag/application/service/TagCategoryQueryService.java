package com.lxp.tag.application.service;

import com.lxp.tag.application.port.provided.usecase.FindCategoryUseCase;
import com.lxp.tag.application.port.query.CategoryResult;
import com.lxp.tag.application.port.required.TagCategoryQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagCategoryQueryService implements FindCategoryUseCase {
    private final TagCategoryQueryPort tagCategoryQueryPort;

    @Override
    public List<CategoryResult> findAll() {
        return tagCategoryQueryPort.findAllWithTags();
    }
}
