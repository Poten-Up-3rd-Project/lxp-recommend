# lxp-recommend API 명세 (최종 확정)

## 1. 내 추천 조회 (External)
GET /api-v1/recommendations/me
Header: X-MEMBER-ID (optional, default: test-member-uuid-001)
Response: ApiResponse<List<RecommendedCourseDto>>

## 2. 추천 갱신 (External)
POST /api-v1/recommendations/refresh
Header: X-MEMBER-ID (required)
Response: ApiResponse<Void>

## 3. 추천 조회 (Internal, 서비스 간 통신)
GET /internal/api-v1/recommendations/{memberId}
Response: List<RecommendedCourseDto> (ApiResponse 래핑 없음)

## 변경 금지!
금요일(1/17) 서버 오픈 전까지 위 URL은 절대 변경하지 않습니다.
