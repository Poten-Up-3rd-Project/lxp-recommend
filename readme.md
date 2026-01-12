

# LXP Recommend Service

학습자의 관심사, 난이도, 학습 이력을 기반으로 강좌를 추천하는 **추천 도메인(Recommend BC)** 전용 마이크로서비스입니다.[3][4]

***

## 1. 이 레포에서 하는 일

- 학습자별 강좌 추천 목록을 생성·조회하는 서비스
- 다른 BC에서 제공하는 데이터(회원, 강좌, 수강 이력)를 읽어서 추천 점수를 계산
- 계산 결과를 자체 DB에 저장해 빠르게 응답

주요 시나리오:
- 프론트가 `GET /api/v1/recommendations/me`로 내 추천 목록 조회
- 회원/수강 이벤트 발생 후 `POST /api/v1/recommendations/refresh`로 추천 재계산 트리거

***

## 2. 기술 스택 & 구조

- **Language**: Java 17
- **Framework**: Spring Boot 3.5.9
- **DB**: MySQL (로컬: Docker Compose)
- **Cache**: Redis
- **Messaging**: RabbitMQ (향후 이벤트 처리 예정)
- **Build**: Gradle
- **아키텍처 스타일**: DDD + 헥사고날(Ports & Adapters) 구조[4][3]

### 레이어 개요

- `domain`: 비즈니스 규칙과 모델 (의존성 없음)
- `application`: 유즈케이스/서비스, Port 인터페이스
- `infrastructure`: DB, 외부 API, 웹 어댑터 등 구현

***

## 3. 디렉터리 구조

```text
src/main/java/com/lxp/recommend
├─ application
│   ├─ dto/                     # 서비스 계층에서 쓰는 DTO
│   ├─ port/
│   │   ├─ provided/            # 도메인에서 기대하는 저장소 등 (Repository Port)
│   │   └─ required/            # 외부 시스템 조회 Port (회원, 강좌, 수강 이력)
│   └─ service/
│       ├─ RecommendCommandService.java  # 추천 계산/갱신 유즈케이스
│       └─ RecommendQueryService.java    # 추천 조회 유즈케이스
│
├─ domain
│   ├─ model/
│   │   ├─ MemberRecommendation.java     # 추천 Aggregate Root
│   │   ├─ RecommendedCourse.java        # 추천된 강좌 항목
│   │   └─ ids/                          # VO (MemberId, CourseId, Level 등)
│   ├─ policy/                           # 점수 계산/난이도 매칭 정책
│   └─ exception/                        # 도메인 예외
│
├─ infrastructure
│   ├─ web/
│   │   └─ RecommendationController.java # REST API 엔드포인트
│   ├─ persistence/
│   │   ├─ entity/                       # JPA 엔티티 (member_recommendations 등)
│   │   ├─ mapper/                       # Entity ↔ Domain 변환
│   │   ├─ repository/                   # Spring Data JPA 인터페이스
│   │   └─ MemberRecommendationRepositoryAdapter.java # Port 구현체
│   ├─ external/
│   │   ├─ user/                         # Member BC 호출 어댑터
│   │   ├─ course/                       # Course BC 호출 어댑터
│   │   └─ enrollment/                   # Enrollment BC 호출 어댑터
│   └─ external/common/                  # 공통 매퍼 (ex: LevelMapper)
│
└─ resources
    ├─ application.yml                   # 기본 설정 (profile 활성화 등)
    ├─ application-local.yml             # 로컬 개발용 설정
    └─ db/migration/                     # Flyway SQL 스크립트
        └─ V1__init_recommend_tables.sql
```

***

## 4. 핵심 도메인 개념

- **MemberRecommendation**
    - 한 명의 학습자에 대한 추천 결과 집합
    - 내부에 여러 `RecommendedCourse`를 포함
    - `updateItems(...)`로 추천 목록을 통째로 교체

- **RecommendedCourse**
    - 추천된 단일 강좌
    - `courseId`, `score`, `rank`를 보유

- **RecommendContext**
    - 추천 계산에 필요한 입력값 묶음
    - 관심 태그, 학습 이력, 추천 후보 강좌 목록 등을 포함

- **ScoringPolicy**
    - 태그 매칭, 난이도 적합성 등을 이용해 점수 계산하는 정책 클래스

***

## 5. 주요 유즈케이스 흐름

### 5.1 내 추천 조회 (`GET /api/v1/recommendations/me`)

1. `RecommendationController`에서 요청 수신
2. `RecommendQueryService.getTopRecommendations(memberId)` 호출
3. `MemberRecommendationRepository`를 통해 저장된 추천 목록 조회
4. `RecommendedCourseDto` 리스트로 변환하여 반환

### 5.2 추천 갱신 (`POST /api/v1/recommendations/refresh`)

1. `RecommendationController`에서 X-MEMBER-ID 헤더로 학습자 ID 수신
2. `RecommendCommandService.refreshRecommendation(learnerId)` 실행
3. 외부 Port를 통해 데이터 수집
    - Member BC: 프로필, 관심 태그, 레벨
    - Course BC: 난이도/태그 기반 강좌 후보
    - Enrollment BC: 학습 이력
4. `RecommendContext` 생성 후, `ScoringPolicy`로 각 후보 강좌에 점수 부여
5. 상위 N개를 추려 `MemberRecommendation` 갱신 및 저장

***

## 6. 외부 연동 포인트 (다른 BC와의 경계)

- **Member BC**
    - Port: `LearnerProfileQueryPort`
    - 역할: 학습자 프로필/관심 태그/레벨 제공

- **Course BC**
    - Port: `CourseMetaQueryPort`
    - 역할: 강좌 메타데이터(난이도, 태그, 공개 여부) 조회

- **Enrollment BC**
    - Port: `LearningHistoryQueryPort`
    - 역할: 학습 이력 조회 (이미 수강한 강좌 제외용)

이 Port들은 `infrastructure.external.*` 패키지의 어댑터 구현으로 실제 HTTP/RabbitMQ/Redis 호출과 연결된다.

***

## 7. 로컬 개발 가이드 (요약)

### 7.1 사전 준비

- JDK 17
- Docker 및 Docker Compose

### 7.2 인프라 기동

레포 루트에서:

```bash
docker-compose up -d
# MySQL: localhost:3307 / DB lxp / user: lxp / pw: lxp
# Redis: localhost:6379
```

### 7.3 애플리케이션 실행

```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

정상 기동 확인:

```bash
curl -UseBasicParsing http://localhost:8080/actuator/health
# status: 200, body: {"status":"UP", ...}
```

***

## 8. 새 팀원이 먼저 보면 좋은 파일들

- `RecommendationController.java`  
  REST API 엔드포인트와 전체 유즈케이스 진입점을 파악할 수 있다.

- `RecommendCommandService.java` / `RecommendQueryService.java`  
  추천 계산/조회 비즈니스 플로우를 이해할 수 있다.

- `domain/model/MemberRecommendation.java` / `RecommendedCourse.java`  
  이 서비스가 관리하는 핵심 도메인 구조를 파악할 수 있다.

- `infrastructure/persistence/*`  
  추천 결과가 DB에 어떻게 저장되는지 확인할 수 있다.

- `application-local.yml` / `db/migration/V1__init_recommend_tables.sql`  
  로컬 환경 설정과 DB 스키마를 함께 볼 수 있다.
