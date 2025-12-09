package com.lxp.tag.presentation.web;

import com.lxp.tag.application.port.provided.usecase.FindCategoryUseCase;
import com.lxp.tag.presentation.web.dto.TagCategoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
public class TagCategoryQueryController {
    private final FindCategoryUseCase findCategoryUseCase;

    @GetMapping
    public ResponseEntity<List<TagCategoryResponse>> getAllCategories() {
        List<TagCategoryResponse> responses = findCategoryUseCase.findAll()
                .stream().map(TagCategoryResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }
}
