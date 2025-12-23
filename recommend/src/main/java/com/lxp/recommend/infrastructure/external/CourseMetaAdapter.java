package com.lxp.recommend.infrastructure.external;

import com.lxp.api.content.course.port.dto.result.CourseInfoResult;
import com.lxp.api.content.course.port.external.ExternalCourseInfoPort;
import com.lxp.api.tag.port.external.TagCachePort;
import com.lxp.recommend.application.port.required.CourseMetaQueryPort;
import com.lxp.recommend.application.dto.CourseMetaData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class CourseMetaAdapter implements CourseMetaQueryPort {

    private final ExternalCourseInfoPort externalCourseInfoPort;
    private final TagCachePort tagCachePort;

    @Override
    public List<CourseMetaData> findByDifficulties(Set<String> targetDifficulties, int limit) {
        // TODO: Course BC에 난이도별 조회 API 추가 필요
        log.warn("[Course BC] 난이도별 조회 API 미구현. 빈 리스트 반환. difficulties={}", targetDifficulties);
        return Collections.emptyList();
    }

    @Override
    public Optional<CourseMetaData> findById(String courseId) {
        try {
            return externalCourseInfoPort.getCourseInfo(courseId)
                    .map(this::toInternalData);
        } catch (Exception e) {
            log.error("[Course BC 호출 실패] courseId={}, error={}", courseId, e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * ACL: CourseInfoResult → CourseMetaData 변환
     */
    private CourseMetaData toInternalData(CourseInfoResult courseInfo) {
        return new CourseMetaData(
                courseInfo.courseUUID(),
                resolveTagNames(courseInfo.tags()),
                courseInfo.difficulty().name(),  // ← Level Enum → String (ACL)
                true
        );
    }

    /**
     * Tag ID 목록 → Tag 이름 목록 변환
     */
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
}
