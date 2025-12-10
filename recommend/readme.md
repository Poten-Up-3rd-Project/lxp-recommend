

## 📋 LXP 추천 기능(Recommendation BC) 작업 브리핑

### 1. 프로젝트 개요
***

**목표:** LXP(Learning Experience Platform)의 개인화 추천 기능 구현  
**담당:** Recommendation Bounded Context  
**기술 스택:**
- Java 17, Spring Boot 3.x, MySQL 8.0, JPA, Gradle
- 아키텍처: **Modulith(멀티모듈)** + **DDD(Domain-Driven Design)** + **Layered Architecture**
- 패키지 루트: `com.lxp.recommend`

***

### 2. 핵심 비즈니스 요구사항

#### 추천 로직 우선순위
- **관심 태그(Interest Tags) > > 난이도(Difficulty Level)**
- 이미 수강 중/완료한 강좌는 추천에서 제외

#### UI 시나리오
- **비로그인:** 최신 강좌 순 나열 (추천 기능 미사용)
- **로그인:** 홈 화면 배너에 개인화 추천 강좌 **최대 4개** 노출

#### 성능 전략
- **사전 계산 + 캐싱:** 추천은 비동기(배치)로 미리 계산하여 DB에 저장
- **1차 필터링(Candidate Generation):** DB에서 후보군 100개만 조회 → 메모리에서 정밀 계산
- **2단계 프로세스:** Fast Filtering(DB) + Ranking(Application)

***

### 3. 설계 결정사항

#### (1) ID 타입: UUID → **Long으로 변경**
- 팀 회의 결정: 성능과 관리 편의성을 위해 `BIGINT AUTO_INCREMENT` 사용
- 도메인 내부에서는 **VO(Value Object)로 감싸기:** `MemberId`, `CourseId`
- 외부 통신(API, Port)은 **원시 타입 `Long`** 사용

#### (2) 패키지 구조: DDD Layered Architecture
```
com.lxp.recommend
├─ domain
│   ├─ model               // 엔티티, VO
│   │   ├─ MemberRecommendation    // Aggregate Root
│   │   ├─ RecommendedCourse      // Value Object (@Embeddable)
│   │   └─ ids
│   │       ├─ MemberId           // String 기반 외부 ID
│   │       └─ CourseId
│   ├─ service            // 도메인 서비스 (순수 비즈니스 로직)
│   │   └─ RecommendationScoringService
│   ├─ repository         // 도메인 저장소 인터페이스
│   │   └─ MemberRecommendationRepository
│   ├─ dto                // 다른 BC에서 가져온 데이터를 담는 View DTO
│   │   ├─ LearnerProfileView
│   │   ├─ CourseMetaView
│   │   ├─ LearningStatusView
│   │   ├─ DifficultyLevel
│   │   └─ EnrollmentStatus
│   └─ event              // (필요 시) 도메인 이벤트
│
├─ application
│   ├─ service
│   │   └─ RecommendationApplicationService
│   ├─ dto
│   │   └─ RecommendedCourseDto   // API 응답용 DTO
│   └─ port
│       └─ required               // Required Ports (Outbound Port)
│           ├─ MemberProfileReader
│           ├─ CourseMetaReader
│           └─ LearningStatusReader
│
├─ presentation
│   └─ RecommendationController   // REST API 엔드포인트
│
├─ infrastructure
│   ├─ adapter                    // Required Port 구현체 (나중에 작성)
│   │   ├─ MemberProfileReaderAdapter
│   │   ├─ CourseMetaReaderAdapter
│   │   └─ LearningStatusReaderAdapter
│   ├─ messaging                  // (이벤트 리스너 등)
│   └─ persistence
│       └─ repository
│           └─ JpaMemberRecommendationRepository // Spring Data JPA 구현체
│
└─ resources
    └─ openapi-recommend.yml      // 추천 API 문서

```

#### (3) `domain.dto` 패키지 도입
- 초기에는 `domain.support`로 설계했으나, 직관성을 위해 **`domain.dto`로 변경**
- 역할: Port가 반환하는 외부 컨텍스트 데이터 뷰(View) 정의

***
#### 4. 구현 완료/미완료 정리 (현재 기준)
   ✅ 구현 완료
   도메인

MemberRecommendation / RecommendedCourse / MemberId, CourseId

RecommendationScoringService

MemberRecommendationRepository (인터페이스)

LearnerProfileView, CourseMetaView, LearningStatusView + Enum들

애플리케이션

RecommendationApplicationService

refreshRecommendationAsync(String memberId)

getTopRecommendations(String memberId)

프레젠테이션

RecommendationController

GET /api/v1/recommendations/me
헤더 X-MEMBER-ID로 memberId(String) 수신 후 서비스 호출

인프라(저장소)

JpaMemberRecommendationRepository

OpenAPI 명세

openapi-recommend.yml에 /recommendations/me 스펙 정의

⏳ 남은 작업 (외부 BC 모두 준비된 상황 기준)
Required Port 구현체 작성 (infrastructure/adapter)

MemberProfileReaderAdapter
→ Member/User BC에서 프로필 조회

CourseMetaReaderAdapter
→ Course BC에서 난이도별 강좌 목록 조회 (최신 100개 제한)

LearningStatusReaderAdapter
→ Enrollment/Learning BC에서 수강 이력 조회

현재는 포트 인터페이스만 있고, 구현체는 비어 있는 상태라
“실제 데이터 연동”을 하려면 이 부분을 채워야 합니다.

이벤트 리스너 (infrastructure/messaging)

예: EnrollmentCreatedEvent, ProfileUpdatedEvent 수신 →
RecommendationApplicationService.refreshRecommendationAsync(memberId) 호출.

스케줄러(선택)

매일 새벽 모든/일부 회원 추천 재계산 (배치).

DB DDL 확인

member_id 컬럼이 VARCHAR로 잘 잡혀 있는지 (String 기반 ID 반영).
***

### 5. API 명세 (프론트엔드 전달용)

**Endpoint:** `GET /api/v1/recommendations/me`  
**인증:** `X-MEMBER-ID` 헤더 (또는 Bearer Token, 추후 확정)  
**응답 예시:**
```json
[
  { "courseId": 12345, "score": 95.5, "rank": 1 },
  { "courseId": 67890, "score": 88.0, "rank": 2 },
  { "courseId": 11111, "score": 82.1, "rank": 3 },
  { "courseId": 22222, "score": 75.0, "rank": 4 }
]
```

***

### 6. 미완료 및 다음 단계

#### 🔜 구현 필요 항목
1.  **Port 구현체 (`infrastructure.external`):**
    - `MemberProfileReaderImpl`: Member 모듈에서 프로필 조회
    - `CourseMetaReaderImpl`: Course 모듈에서 강좌 메타 조회
    - `LearningStatusReaderImpl`: Learning 모듈에서 수강 이력 조회

2.  **비동기 처리 설정:**
    - 메인 애플리케이션에 `@EnableAsync` 추가
    - 필요 시 `ThreadPoolTaskExecutor` 설정

3.  **이벤트 리스너 구현:**
    - 강좌 완료 이벤트 → `refreshRecommendationAsync` 트리거
    - 프로필 수정 이벤트 → 추천 재계산

4.  **DDL 스크립트 작성 (DB-First 정책):**
    - `member_recommendations`, `recommended_course_items` 테이블 생성 스크립트

5.  **통합 테스트:**
    - Controller → Service → Repository 전체 흐름 검증

***

### 7. 주요 설계 원칙 준수 사항

- ✅ **DIP(의존성 역전):** 도메인이 인프라에 의존하지 않음 (Port 사용)
- ✅ **계층 분리:** Presentation → Application → Domain → Infrastructure 의존 방향 엄수
- ✅ **VO 사용:** 원시 타입 집착 방지, 도메인 개념 명확화
- ✅ **CQRS 스타일:** Command(쓰기) / Query(읽기) 메서드 분리
- ✅ **성능 최적화:** Candidate Generation + Caching 전략 적용

***

이상이 현재까지 완료된 추천 기능 설계 및 구현 내용입니다. 
다음 작업으로 Port 구현 및 이벤트 처리가 예상됩니다. 

##  ERD 요구사항 

***

### 1. ERD 설계 원칙 (팀 결정 반영)

1.  **PK 타입:** `BIGINT (AUTO_INCREMENT)` 사용 (UUID 아님).
2.  **Member/Course 연동:** `member_id`, `course_id`는 FK 제약조건을 걸 수도 있지만, MSA/Modulith의 느슨한 결합을 위해 **논리적 연관(값만 저장)**만 하고 강제적 FK(Foreign Key Constraint)는 생략하는 경우가 많습니다. (여기서는 물리적 FK는 생략하고 인덱스만 거는 방식을 권장합니다.)
3.  **데이터 구조:**
    *   `member_recommendations`: 추천 결과의 메타 정보 (누구의 추천인지, 언제 계산했는지).
    *   `recommended_course_items`: 실제 추천된 강좌 리스트 (값 컬렉션).

***

### 2. ERD 다이어그램 (Mermaid)


***

### 3. MySQL DDL 스크립트

프로젝트 정책인 **'Database-First'** 접근에 맞춰, 실제 실행 가능한 DDL 스크립트를 작성했습니다.

***

### 4. 설계 포인트 설명

#### (1) `member_recommendations`
*   **`member_id` (UNIQUE):** 한 회원당 하나의 추천 결과만 유지합니다. 새로운 추천 결과가 생기면 기존 row의 `calculated_at`을 갱신하거나, `items`를 갈아끼우는 방식입니다. (JPA의 `updateItems` 메서드 동작 방식)

#### (2) `recommended_course_items`
*   **`item_index`:** JPA의 `@OrderColumn`을 사용했기 때문에, 리스트의 순서를 보장하기 위한 컬럼이 필수입니다.
*   **`ON DELETE CASCADE`:** 부모인 `member_recommendations`가 삭제되면(회원 탈퇴 등으로), 딸린 추천 아이템들도 자동으로 삭제되도록 설정했습니다.
*   **`course_id`:** 물리적 FK를 걸지 않았습니다. Course 모듈이 독립적으로 배포되거나 DB가 분리될 가능성을 고려하여, **논리적인 참조(ID 값만 저장)**만 유지합니다.

# 인수인계용

***

## 추천 BC – CourseMetaReader 관련 현황 정리 1209

### 1. 현재 설계 상태

- 추천 BC는 **CourseMetaReader 인터페이스**만 정의해둔 상태입니다:

```java
public interface CourseMetaReader {

    List<CourseMetaView> findByDifficulties(Set<DifficultyLevel> difficulties);
}
```

- 구현체 `CourseMetaReaderImpl`은 **아직 만들지 않았습니다.**
- 이유:
    - Course BC의 도메인/리포지토리/API 설계가 완전히 확정되지 않았고,
    - 추천 BC에서는 **다른 BC의 내부 코드에 직접 의존하지 않기로 한 원칙**(느슨한 결합, MSA 대비)을 지키기 위해서입니다.

***

### 2. 현재 추천 로직에서의 사용 방식

- `RecommendationApplicationService`는 현재 `findByDifficulties(...)`를 호출하여 **후보군 전체를 가져오는 구조**입니다.
- 앞으로는 **성능을 위해 "최신 100개"까지만 받아오는 형태로 개선**할 예정입니다.
- 다만 지금은:
    - **구현체가 없기 때문에 실제 DB 접근은 되지 않는 상태**이고,
    - 인터페이스 수준에서만 설계가 되어 있습니다.

***

### 3. 향후 해야 할 일 (후임자/유지보수 담당자에게)

1. **CourseMetaReader 인터페이스 확장 (limit 추가)**  
   강좌 수 증가를 대비해, 호출자가 최대 개수를 조절할 수 있도록 인터페이스를 변경해야 합니다:

   ```java
   List<CourseMetaView> findByDifficulties(Set<DifficultyLevel> difficulties, int limit);
   ```

2. **CourseMetaReaderImpl 구현 (infrastructure 계층)**
    - 위치 예시:  
      `com.lxp.recommend.infrastructure.course.CourseMetaReaderImpl`
    - 역할:
        - Course BC가 제공하는 수단(JPA Repository, REST API, Feign Client 등)을 이용해
        - 특정 난이도에 해당하는 강좌들을 **최신순으로 최대 100개까지 조회**하여 `CourseMetaView`로 변환.
    - 구현 시점:
        - Course BC의 스키마/엔티티/API가 확정된 이후,
        - 팀 합의된 통신 방식(내부 모듈 직접 참조 vs HTTP 호출 등)에 맞춰 구현.

3. **수강 중 강좌 태그를 활용한 가중치 고도화**
    - 현재 설계에서는:
        - 1차 후보군(최신 100개)에서 **Implicit Tag(수강 중 강좌 태그)**를 수집하는 방식으로 가정.
    - 더 정확한 구현을 위해서는:
        - 수강 중인 강좌 ID 목록으로 **별도의 `findAllByIds(Set<String>)` 메서드**를 추가하고,
        - 그 메서드를 통해 **수강 중 강좌 메타 정보를 다시 조회**한 뒤 태그를 수집하는 방향으로 확장할 수 있습니다.
    - 이 부분은 **향후 성능/정확도 요구에 따라 선택적으로 도입**할 수 있습니다.



사용자 구분: LearnerLevel.JUNIOR / MIDDLE / SENIOR / EXPERT

추천 로직:

JUNIOR → JUNIOR, MIDDLE

MIDDLE → MIDDLE, SENIOR

SENIOR → SENIOR, EXPERT

EXPERT → EXPERT만 (더 높은 단계 없음)