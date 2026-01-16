📌 Phase 3: Passport 인증 통합 (오후)
3.1 구현된 컴포넌트
디렉토리 구조:
```text
infrastructure/
├── constants/
│   └── PassportConstants.java                 상수 정의
└── web/external/passport/
    ├── config/
    │   ├── KeyProperties.java                  Secret Key 설정
    │   └── PassportConfig.java                Security 설정
    ├── filter/
    │   ├── PassportAuthenticationFilter.java   인증 필터
    │   └── PassportAuthenticationEntryPoint.java  실패 처리
    ├── support/
    │   ├── PassportExtractor.java              헤더 추출
    │   └── PassportVerifier.java               JWT 검증
    ├── model/
    │   └── PassportClaims.java                 클레임 모델
    └── exception/
        └── InvalidPassportException.java       예외 처리


```

### 3.2 Passport 처리 흐름
```text
1. API Gateway
   └─ X-Passport 헤더 추가 (JWT)
   
2. Recommend BC
   └─ PassportAuthenticationFilter
      ├─ PassportExtractor: 헤더 추출
      ├─ PassportVerifier: JWT 검증
      └─ SecurityContext 설정
   
3. Controller
   └─ @AuthenticationPrincipal로 userId 접근

```
### 프로파일 분리
테스트 환경 (test):

✅ Mockito @Mock 사용
❌ 실제 API 어댑터 비활성화
❌ WebClient Bean 생성 안 됨

로컬/운영 환경 (!test):

❌ Mock 어댑터 없음
✅ 실제 API 어댑터 활성화 (@Profile("!test"))
✅ WebClient Bean 생성