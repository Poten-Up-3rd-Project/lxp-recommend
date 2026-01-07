package com.lxp.recommend.infrastructure.external;

// ❌ 주석 처리 (api 모듈 삭제됨)
// import com.lxp.api.content.course.port.dto.result.CourseInfoResult;
// import com.lxp.api.content.course.port.external.ExternalCourseInfoPort;
// import com.lxp.api.tag.port.external.TagCachePort;

import com.lxp.recommend.application.port.required.CourseMetaQueryPort;
import com.lxp.recommend.application.dto.CourseMetaData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class CourseMetaAdapter implements CourseMetaQueryPort {

    // ❌ 의존성 주석 처리
    // private final ExternalCourseInfoPort externalCourseInfoPort;
    // private final TagCachePort tagCachePort;

    @Override
    public List<CourseMetaData> findByDifficulties(Set<String> targetDifficulties, int limit) {
        log.warn("[Course BC] Mock: 빈 리스트 반환. difficulties={}", targetDifficulties);
        return Collections.emptyList();
    }

    @Override
    public Optional<CourseMetaData> findById(String courseId) {
        log.info("[Course BC] Mock 실행. courseId={}", courseId);
        // Mock 데이터 반환
        CourseMetaData mockData = new CourseMetaData(
                courseId,
                Set.of("Java", "Spring"),
                "MIDDLE",
                true
        );
        return Optional.of(mockData);

        /* TODO: api 모듈 배포 후 주석 해제
        try {
            return externalCourseInfoPort.getCourseInfo(courseId)
                    .map(this::toInternalData);
        } catch (Exception e) {
            log.error("[Course BC 호출 실패] courseId={}, error={}", courseId, e.getMessage(), e);
            return Optional.empty();
        }
        */
    }

    /* 주석 처리
    private CourseMetaData toInternalData(CourseInfoResult courseInfo) {
        return new CourseMetaData(
                courseInfo.courseUUID(),
                resolveTagNames(courseInfo.tags()),
                courseInfo.difficulty().name(),
                true
        );
    }

    private Set<String> resolveTagNames(List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return Set.of();
        }
        try {
            return tagCachePort.findByIds(Set.copyOf(tagIds)).stream()
                    .filter(tagResult -> "ACTIVE".equals(tagResult.state()))
                    .map(tagResult -> tagResult.name())
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            log.error("[Tag 조회 실패] tagIds={}, error={}", tagIds, e.getMessage(), e);
            return Set.of();
        }
    }
    */
}
