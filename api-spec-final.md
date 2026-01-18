# lxp-recommend API 명세 

Header: X-Passport, Passport JWT에서 자동으로 userId 추출
X-Passport 헤더 (JWT 인증)

## 1. 내 추천 조회 (External)
GET /api-v1/recommendations/me
Header: X-Passport 

Passport JWT에서 userId 자동 추출
추천 없을 시: 빈 배열 [] 반환
최대 10개 제한
score 내림차순, rank 오름차순 정렬

## 2. 추천 갱신 (External)
POST /api-v1/recommendations/refresh
Header: X-Passport: 

Passport JWT에서 userId 추출
외부 BC 호출 (Member, Course, Enrollment)
추천 계산 후 DB 저장
202 Accepted 반환 (비동기 처리)

## 3. 추천 조회 (Internal, 서비스 간 통신)
GET /internal/api-v1/recommendations/{memberId}
Header: X-Passport

ApiResponse 래핑 없음 (직접 배열 반환)
추천 없을 시: 빈 배열 [] 반환 (404 아님)
Gateway가 Passport 발급 및 전달

### 변경 사항
헤더만 변경: X-MEMBER-ID → X-Passport
URL 유지: 모든 엔드포인트 동일
응답 형식 유지: ApiResponse 구조 동일
