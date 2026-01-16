# API 데이터 검증 체크리스트

## External API (/api-v1/recommendations/me)
- [ ] X-MEMBER-ID 헤더 처리
- [ ] 빈 배열 응답 (추천 없을 시)
- [ ] 최대 10개 제한
- [ ] score, rank 정렬 확인

## Internal API (/internal/api-v1/recommendations/{memberId})
- [ ] ResultDto 직접 반환 (ApiResponse 없음)
- [ ] 404 vs 빈 배열 정책 확인

## 외부 BC 연동
- [ ] Member API: /internal/api-v1/members/{id}/profile
- [ ] Course API: /internal/api-v1/courses/search?difficulties=...
- [ ] Enrollment API: /internal/api-v1/enrollments/learner/{id}

## 데이터 필수 필드 확인
- [ ] LearnerProfileData: memberId, interestTags, learnerLevel
- [ ] CourseMetaData: courseId, tags, difficulty, isPublic
- [ ] LearningHistoryData: courseId, status
