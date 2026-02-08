package com.lxp.recommend.application.service;

import com.lxp.recommend.application.port.in.RecommendQueryUseCase;
import com.lxp.recommend.application.port.out.ResultRepository;
import com.lxp.recommend.application.port.out.UserRepository;
import com.lxp.recommend.domain.result.entity.RecommendResult;
import com.lxp.recommend.domain.user.entity.RecommendUser;
import com.lxp.recommend.dto.response.RecommendApiResponse;
import com.lxp.recommend.global.exception.BusinessException;
import com.lxp.recommend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommendQueryService implements RecommendQueryUseCase {

    private static final int DEFAULT_LIMIT = 7;
    private static final int MAX_LIMIT = 20;

    private final ResultRepository resultRepository;
    private final UserRepository userRepository;

    @Override
    @Cacheable(value = "recommendations", key = "#userId + ':' + #limit", unless = "#result == null")
    public RecommendApiResponse getRecommendations(String userId, Integer limit) {
        log.info("Fetching recommendations for user: {} (cache miss)", userId);

        int actualLimit = resolveLimit(limit);

        RecommendResult result = resultRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RECOMMENDATION_NOT_FOUND,
                        "No recommendations found for user: " + userId
                ));

        RecommendUser user = userRepository.findById(userId).orElse(null);

        List<String> courseIds = result.getCourseIds();

        if (user != null) {
            Set<String> excludeIds = new HashSet<>();
            excludeIds.addAll(user.getEnrolledCourseIds());
            excludeIds.addAll(user.getCreatedCourseIds());

            courseIds = courseIds.stream()
                    .filter(id -> !excludeIds.contains(id))
                    .limit(actualLimit)
                    .toList();
        } else {
            courseIds = courseIds.stream()
                    .limit(actualLimit)
                    .toList();
        }

        return RecommendApiResponse.of(userId, courseIds, result.getBatchId(), result.getUpdatedAt());
    }

    private int resolveLimit(Integer limit) {
        if (limit == null || limit <= 0) {
            return DEFAULT_LIMIT;
        }
        return Math.min(limit, MAX_LIMIT);
    }
}
