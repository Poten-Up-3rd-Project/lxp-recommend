package com.lxp.recommend.infrastructure.web.internal;

import com.lxp.recommend.application.dto.RecommendedCourseDto;
import com.lxp.recommend.application.service.RecommendQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/internal/api-v1/recommendations")
public class InternalRecommendationController {
}
