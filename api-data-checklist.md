# API 데이터 검증 체크리스트

## External API (/api-v1/recommendations/me)

- [ ] 빈 배열 응답 (추천 없을 시)
- [ ] 최대 10개 제한
- [ ] score, rank 정렬 확인


## Internal API (/internal/api-v1/recommendations/{memberId})
- [ ] ResultDto 직접 반환 (ApiResponse 없음)
- [ ] 404 vs 빈 배열 정책 확인

## 외부 BC 연동
Member API 호출 (FeignClient)
Course API 호출 (FeignClient)
Enrollment API 호출 (FeignClient)
Passport 자동 전달 (FeignHeaderForwardInterceptor)

## 데이터 필수 필드 확인
- [ ] LearnerProfileData: memberId, interestTags, learnerLevel
- [ ] CourseMetaData: courseId, tags, difficulty, isPublic
- [ ] LearningHistoryData: courseId, status

### 변경 사항
헤더만 변경: X-MEMBER-ID → X-Passport
URL 유지: 모든 엔드포인트 동일
응답 형식 유지: ApiResponse 구조 동일