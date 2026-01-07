

## 📋 LXP 추천 기능(Recommendation BC) 작업 브리핑

### 1. 프로젝트 개요
***

**목표:** LXP(Learning Experience Platform)의 개인화 추천 기능 구현  
**담당:** Recommendation Bounded Context  
**기술 스택:**
- Java 17, Spring Boot 4.0.0, MySQL 9.0, JPA, Gradle
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

2.2 설계 원칙 (팀 규약 포함)
A. 계층별 책임
B. 의존성 방향 (헥사고날 원칙)
C. 팀 규약 준수 사항
✅ 폴더 구조: domain/, application/, infrastructure/, interfaces/
✅ Port 용어: required (외부 필요), provided (외부 제공)
✅ 기존 객체 이름 유지 (예: LearningStatusView → 이름은 그대로, 역할만 명확화)
✅ POJO/JPA 완전 분리 (도메인은 순수 Java, JPA는 infrastructure에만)

#### (2) 패키지 구조: DDD Layered Architecture


```
3. 최종 패키지 구조
│ │
│ ├─ exception/ # 도메인 예외
│ │ ├─ RecommendationException.java
│ │ ├─ InvalidRecommendationContextException.java
│ │ └─ RecommendationLimitExceededException.java
│ │
│ └─ dto/ # 도메인 DTO (Enum, 단순 데이터 구조)
│ ├─ Level.java # Enum
│ ├─ LearnerLevel.java # Enum
│ └─ EnrollmentStatus.java # Enum
│
├─ application/
│ ├─ port/
│ │ ├─ required/ # Outbound Port (외부로부터 필요)
│ │ │ ├─ MemberProfileReader.java
│ │ │ ├─ CourseMetaReader.java
│ │ │ └─ LearningStatusReader.java
│ │ │
│ │ └─ provided/ # Inbound Port (외부에 제공)
│ │ └─ RefreshRecommendationUseCase.java
│ │
│ ├─ service/ # Application Service
│ │ └─ RecommendationApplicationService.java # 유스케이스 조율
│ │
│ └─ dto/ # Application DTO (외부 통신용)
│ ├─ RecommendedCourseDto.java # API 응답용
│ └─ LearnerProfileView.java # Port 통신용 (외부 BC 데이터 수신)
│
├─ infrastructure/
│ ├─ adapter/ # Adapter 구현체
│ │ ├─ MemberProfileReaderAdapter.java
│ │ ├─ CourseMetaReaderAdapter.java
│ │ └─ LearningStatusReaderAdapter.java
│ │
│ ├─ persistence/
│ │ ├─ jpa/
│ │ │ ├─ entity/ # JPA 전용 엔티티
│ │ │ │ ├─ MemberRecommendationJpaEntity.java
│ │ │ │ └─ RecommendedCourseItemJpaEntity.java
│ │ │ │
│ │ │ ├─ repository/ # Spring Data JPA Repository
│ │ │ │ └─ JpaMemberRecommendationRepository.java
│ │ │ │
│ │ │ └─ mapper/ # JPA ↔ Domain 변환
│ │ │ └─ MemberRecommendationMapper.java
│ │ │
│ │ └─ adapter/ # Repository Adapter
│ │ └─ MemberRecommendationRepositoryAdapter.java
│ │
│ └─ scheduler/ # 배치 작업
│ └─ RecommendationBatchScheduler.java
│
└─ interfaces/ # Presentation Layer (HTTP)
└─ rest/
└─ RecommendationController.java

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

⏳ 남은 작업 (외부 BC 모두 준비된 상황 기준 1211)
infrastructure /  application 계층 수정될 예정입니다. 
[ ] 도메인 서비스 리팩토링 
[ ] 애플리케이션 서비스 리팩토링
[ ] Repository Adapter 수정

[ ] 도메인 서비스 테스트 작성
[ ] 애플리케이션 서비스 테스트 작성
[ ] 통합 테스트 작성

[ ] 배치 스케줄러 추가
[ ] Member BC와 협의 (전체 회원 목록 API)

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

    List<CourseMetaView> findByDifficulties(Set<Level> difficulties);
}
```



### 2. 현재 추천 로직에서의 사용 방식

- `RecommendationApplicationService`는 현재 `findByDifficulties(...)`를 호출하여 **후보군 전체를 가져오는 구조**입니다.
- 앞으로는 **성능을 위해 "최신 100개"까지만 받아오는 형태로 개선**할 예정입니다.


### 3. 향후 해야 할 일 (후임자/유지보수 담당자에게)

1. **CourseMetaReader 인터페이스 확장 (limit 추가)**  
   강좌 수 증가를 대비해, 호출자가 최대 개수를 조절할 수 있도록 인터페이스를 변경해야 합니다:

   ```java
   List<CourseMetaView> findByDifficulties(Set<Level> difficulties, int limit);
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



####  🎯 Recommend BC Port/Adapter 설계 전략
핵심 원칙
Port는 100% Recommend BC 용어 (외부 의존 제로)

Adapter는 ACL 역할 (외부 → 내부 변환)

MSA 전환 시 Adapter만 교체 (Port는 불변)

# 4개월차 추천 담당 참고사항 
level enum -> Option 1: ACL에서만 변환 (권장) ⭐
1. Domain 레벨 정의 (기존 유지 또는 간소화)
   Option 1-A: 기존 Enum 유지 (가장 안전)
   recommend/domain/dto/LearnerLevel.java (변경 없음)
2. recommend/domain/dto/Level.java (변경 없음)
원칙:

Domain은 common.Level에 의존하지 않음
Adapter(ACL)에서만 common.Level → domain.Level 변환

장점:

Domain 독립성 유지
MSA 전환 시 유리 (common 패키지 제거 가능)

