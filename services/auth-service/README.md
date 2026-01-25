# Auth Service

JWT 기반 인증 및 토큰 발급을 담당하는 서비스입니다.

## 목차

- [개요](#개요)
- [주요 기능](#주요-기능)
- [기술 스택](#기술-스택)
- [아키텍처](#아키텍처)
- [JWT 토큰 메커니즘](#jwt-토큰-메커니즘)
- [인증 플로우](#인증-플로우)
- [보안 특성](#보안-특성)
- [API 명세](#api-명세)
- [기술적 성과](#기술적-성과)

## 개요

Auth Service는 분산 시스템에서 중앙 집중식 인증을 담당하는 서비스로, JWT 토큰 발급 및 검증을 통해 stateless한 인증 체계를 구현합니다.

### 포트 정보
- **로컬 개발**: 8089
- **프로덕션**: 8089

### 데이터베이스
- **스키마**: commerce-auth
- **포트**: 3309 (로컬)

## 주요 기능

### 1. 사용자 인증 정보 등록
- user-service와 연계하여 사용자 인증 정보 생성
- BCrypt 기반 비밀번호 암호화
- LOCAL, KAKAO 등 다중 인증 제공자 지원

### 2. 로그인 및 토큰 발급
- 이메일/비밀번호 기반 로그인
- JWT Access Token 및 Refresh Token 발급
- HttpOnly 쿠키를 통한 Refresh Token 전달

### 3. 토큰 관리
- Access Token: 24시간 유효
- Refresh Token: 7일 유효
- Refresh Token DB 저장을 통한 세션 관리

## 기술 스택

| 카테고리 | 기술 |
|---------|------|
| 언어 | Kotlin 1.9.25 |
| 프레임워크 | Spring Boot 3.5.8, Spring Security |
| 데이터베이스 | MariaDB (JPA) |
| 인증 | JWT (jjwt 0.12.5), BCrypt |
| 문서화 | SpringDoc OpenAPI 3 |
| 모니터링 | Actuator, Prometheus |

## 아키텍처

### Clean Architecture 구조

```
auth-service/
├── api/                     # 프레젠테이션 계층
│   ├── controller/          # REST API 엔드포인트
│   ├── dto/request/         # 요청 DTO
│   └── dto/response/        # 응답 DTO
├── application/             # 애플리케이션 계층
│   ├── usecase/            # 비즈니스 로직 (LoginUseCase, RegisterUseCase)
│   ├── port/               # 외부 의존성 인터페이스
│   └── dto/                # 애플리케이션 DTO
├── domain/                  # 도메인 계층
│   ├── entity/             # 엔티티 (UserAuth, LoginHistory)
│   ├── vo/                 # Value Object (Email, EncryptedPassword)
│   └── enums/              # 열거형 (UserRole, AuthProvider)
└── infra/                   # 인프라 계층
    ├── persist/            # JPA 리포지토리 및 어댑터
    ├── security/           # JWT 토큰 생성기
    ├── config/             # 설정 클래스
    └── filter/             # 필터 (CorrelationId, UserContext)
```

### 의존성 규칙

- **application/domain → api/infra 의존 금지**
- 포트-어댑터 패턴을 통한 의존성 역전
  - `TokenGeneratorPort` (application) ← `JwtTokenGeneratorAdapter` (infra)
  - `AuthPersistPort` (application) ← `AuthPersistAdapter` (infra)

## JWT 토큰 메커니즘

### 토큰 구조

#### Access Token
```json
{
  "sub": "1",              // 사용자 ID
  "email": "user@example.com",
  "roles": ["ROLE_USER"],
  "type": "access",
  "iat": 1234567890,       // 발급 시간
  "exp": 1234654290        // 만료 시간 (24시간 후)
}
```

#### Refresh Token
```json
{
  "sub": "1",              // 사용자 ID
  "type": "refresh",
  "iat": 1234567890,
  "exp": 1235172690        // 만료 시간 (7일 후)
}
```

### 토큰 생성 과정

1. **SecretKey 검증**: 256비트(32바이트) 이상 요구
2. **Payload 구성**: 사용자 정보 및 권한 포함
3. **서명 생성**: HMAC-SHA256 알고리즘 사용
4. **토큰 발급**: Base64 인코딩된 JWT 문자열 반환

```kotlin
// JwtTokenGeneratorAdapter.kt 핵심 로직
val accessToken = Jwts.builder()
    .subject(userId.toString())
    .claim("email", email)
    .claim("roles", roles)
    .claim("type", "access")
    .issuedAt(now)
    .expiration(accessTokenExpiration)
    .signWith(secretKey)
    .compact()
```

### 토큰 검증

- **서명 검증**: SecretKey를 이용한 HMAC 검증
- **만료 시간 검증**: 현재 시간과 exp claim 비교
- **타입 검증**: access/refresh 구분

## 인증 플로우

### 1. 사용자 등록 플로우

```
[User] → [user-service] → [auth-service]
   |           |                  |
   |      회원가입 요청          |
   |           |                  |
   |      사용자 생성             |
   |           |                  |
   |           |  POST /api/auth  |
   |           |  (내부 API)       |
   |           |                  |
   |           |              인증 정보 저장
   |           |              (BCrypt 암호화)
   |           |                  |
   |      ← 생성 완료 ←           |
```

**특징**:
- user-service 전용 내부 API (Swagger에서 Hidden)
- 비밀번호는 BCrypt로 암호화되어 저장
- Email은 Value Object로 형식 검증

### 2. 로그인 플로우

```
[User] → [auth-service]
   |           |
로그인 요청    |
   |      이메일/비밀번호 검증
   |      (BCrypt.matches)
   |           |
   |      JWT 토큰 발급
   |      (Access + Refresh)
   |           |
   |      Refresh Token DB 저장
   |           |
   | ← Access Token (Header)
   | ← Refresh Token (HttpOnly Cookie)
```

**Response Headers**:
```http
Authorization: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Set-Cookie: refreshToken=...; HttpOnly; Secure; SameSite=Strict; Path=/; Max-Age=604800
```

### 3. 인증 검증 플로우

```
[Client] → [다른 서비스]
   |            |
   | Authorization: Bearer <token>
   |            |
   |       JWT 필터 (common-security)
   |            |
   |       토큰 서명 검증
   |       만료 시간 검증
   |            |
   |       SecurityContext에 사용자 정보 저장
   |            |
   |       비즈니스 로직 실행
   |       (@AuthId로 사용자 ID 주입)
```

**특징**:
- common-security 모듈의 자동 설정으로 모든 서비스에 적용
- stateless 인증 (서버에 세션 저장 불필요)
- 각 서비스는 동일한 SecretKey로 독립적으로 검증

## 보안 특성

### 1. 비밀번호 암호화

**BCrypt 알고리즘 사용**:
- 솔트 자동 생성
- 느린 해싱으로 무차별 대입 공격 방어
- 동일 비밀번호도 매번 다른 해시값 생성

```kotlin
// AuthSecurityConfig.kt
@Bean
fun passwordEncoder(): DelegatingPasswordEncoder = DelegatingPasswordEncoder(
    "bcrypt",
    mapOf("bcrypt" to BCryptPasswordEncoder())
)
```

### 2. Value Object를 통한 도메인 보호

**Email Value Object**:
- 정규식 기반 형식 검증
- 불변성 보장 (Kotlin inline value class)
- JPA Converter를 통한 투명한 영속성

```kotlin
@JvmInline
value class Email private constructor(val value: String) {
    init {
        require(isValid(value)) { "Invalid email format: $value" }
    }

    companion object {
        private val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$".toRegex()

        fun of(rawEmail: String?): Email {
            if (rawEmail.isNullOrBlank()) {
                throw BaseException(CommonErrorCode.INVALID_INPUT, "Email cannot be empty")
            }
            return Email(rawEmail)
        }
    }
}
```

**EncryptedPassword Value Object**:
- 암호화된 비밀번호만 허용
- 평문 비밀번호와 타입 수준에서 구분

### 3. Refresh Token 전략

**HttpOnly 쿠키 사용**:
- XSS 공격 방어 (JavaScript로 접근 불가)
- Secure 플래그로 HTTPS 강제
- SameSite=Strict로 CSRF 방어

**DB 저장을 통한 세션 관리**:
- 로그아웃 시 Refresh Token 무효화 가능
- 탈취된 토큰 강제 만료 가능
- 다중 디바이스 로그인 추적 가능

### 4. 토큰 만료 시간 설정

| 토큰 종류 | 만료 시간 | 용도 |
|----------|----------|------|
| Access Token | 24시간 | API 요청 인증 |
| Refresh Token | 7일 | Access Token 재발급 |

**전략적 이유**:
- Access Token 짧은 유효 기간: 토큰 탈취 시 피해 최소화
- Refresh Token 긴 유효 기간: 사용자 경험 향상 (재로그인 빈도 감소)
- DB 저장으로 보안성 보완

### 5. 에러 처리 보안

**인증 실패 시 동일한 에러 메시지 반환**:
```kotlin
// LoginUseCase.kt
val userAuth = authPersistPort.findByEmail(toDto.email)
    ?: throw NotFoundException(AuthErrorCode.PROVIDER_USER_NOT_FOUND)

if (!passwordEncoder.matches(toDto.password, userAuth.password.value)) {
    throw NotFoundException(AuthErrorCode.PROVIDER_USER_NOT_FOUND)  // 동일한 에러
}
```

**이유**: 이메일 존재 여부 노출 방지 (사용자 열거 공격 방어)

## API 명세

### 1. POST /api/auth

**사용자 인증 정보 등록** (내부 API)

**Request**:
```json
{
  "userId": 1,
  "email": "user@example.com",
  "provider": "LOCAL",
  "password": "rawPassword123!",
  "role": "ROLE_USER"
}
```

**Response**:
```json
{
  "success": true,
  "data": null
}
```

### 2. POST /api/auth/login

**로컬 사용자 로그인**

**Request**:
```json
{
  "email": "user@example.com",
  "password": "password123!"
}
```

**Response**:
```json
{
  "success": true,
  "data": null
}
```

**Response Headers**:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Set-Cookie: refreshToken=...; HttpOnly; Secure; SameSite=Strict
```

## 기술적 성과

### 1. Stateless 인증 아키텍처 구현

**성과**:
- JWT 기반 분산 인증 시스템 설계 및 구현
- 각 서비스가 독립적으로 토큰 검증 (auth-service에 의존하지 않음)
- 수평 확장에 유리한 stateless 구조

**기술적 의사결정**:
- Access Token은 stateless (빠른 검증)
- Refresh Token은 DB 저장 (보안성 강화)

### 2. 계층형 아키텍처와 의존성 역전

**성과**:
- Clean Architecture 원칙 준수
- 포트-어댑터 패턴으로 테스트 용이성 확보
- 도메인 로직과 인프라 로직 명확한 분리

**구현 예시**:
```kotlin
// Port (application)
interface TokenGeneratorPort {
    fun generateTokens(userId: Long, email: String, roles: List<String>): AuthTokenDto
}

// Adapter (infra)
@Component
class JwtTokenGeneratorAdapter(private val jwtProperties: JwtProperties) : TokenGeneratorPort {
    override fun generateTokens(...): AuthTokenDto { ... }
}
```

### 3. Value Object를 활용한 도메인 모델링

**성과**:
- Kotlin inline value class로 런타임 오버헤드 없이 타입 안전성 확보
- Email, EncryptedPassword 등 도메인 개념을 타입으로 표현
- JPA Converter로 영속성 계층과 투명하게 통합

**장점**:
- 컴파일 타임에 타입 검증 (rawPassword vs encryptedPassword 혼용 방지)
- 비즈니스 규칙 캡슐화 (Email 형식 검증)
- 코드 가독성 향상

### 4. 보안 최적화

**성과**:
- BCrypt로 무차별 대입 공격 방어
- HttpOnly 쿠키로 XSS 공격 방어
- SameSite=Strict로 CSRF 공격 방어
- 동일 에러 메시지로 사용자 열거 공격 방어

**보안 체크리스트**:
- [x] 비밀번호 암호화 (BCrypt)
- [x] JWT SecretKey 256비트 이상
- [x] Refresh Token DB 저장
- [x] HttpOnly 쿠키 사용
- [x] 인증 실패 시 정보 노출 방지

### 5. 공통 모듈 통합

**성과**:
- common-core의 에러 처리 및 응답 형식 활용
- common-security의 JWT 필터와 연계
- 일관된 API 응답 형식 (`ApiResponse.success()`)

**통합 지점**:
- `@UseCase` 어노테이션으로 트랜잭션 관리
- `AuthErrorCode`로 도메인별 에러 코드 정의
- `ApiResponse`로 표준 응답 래핑

### 6. 관찰 가능성 (Observability)

**성과**:
- Actuator를 통한 헬스 체크 및 메트릭 노출
- Prometheus 연동으로 메트릭 수집
- Logback을 통한 구조화된 로깅

**노출 엔드포인트**:
- `/actuator/health`: 서비스 상태
- `/actuator/prometheus`: 메트릭 수집
- `/actuator/info`: 서비스 정보

### 7. 문서화

**성과**:
- SpringDoc OpenAPI 3로 자동화된 API 문서
- Swagger UI로 인터랙티브한 API 테스트 환경 제공
- 내부/외부 API 명확한 구분 (`@Hidden` 어노테이션)

**접근 경로**:
- Swagger UI: http://localhost:8089/swagger-ui.html
- OpenAPI Spec: http://localhost:8089/v3/api-docs

## 환경 변수

| 변수명 | 기본값 | 설명 |
|-------|-------|------|
| SERVER_PORT | 8080 | 서버 포트 |
| DB_HOST | localhost | MariaDB 호스트 |
| DB_PORT | 3309 | MariaDB 포트 |
| DB_NAME | commerce-auth | 데이터베이스 스키마 |
| DB_USERNAME | admin | 데이터베이스 사용자명 |
| DB_PASSWORD | admin1234 | 데이터베이스 비밀번호 |
| JWT_SECRET | (기본값 제공) | JWT 서명 키 (256비트 이상) |
| JWT_EXPIRATION | 86400 | Access Token 만료 시간 (초) |
| JWT_REFRESH_EXPIRATION | 604800 | Refresh Token 만료 시간 (초) |

## 빌드 및 실행

```bash
# 빌드
./gradlew :services:auth-service:build

# 테스트
./gradlew :services:auth-service:test

# 로컬 실행
./gradlew :services:auth-service:bootRun

# Docker 이미지 빌드
cd services/auth-service
docker build -t auth-service:latest .
```

## 의존성

### 공통 모듈
- `common-core`: 에러 처리, 응답 형식, 유틸리티
- `common-security`: JWT 검증 필터, @AuthId 주입

### 주요 라이브러리
- `spring-boot-starter-security`: Spring Security
- `jjwt-api`, `jjwt-impl`: JWT 생성 및 검증
- `spring-boot-starter-data-jpa`: JPA 영속성
- `mariadb-java-client`: MariaDB 드라이버

## 참고 사항

- **내부 API 보안**: `/api/auth` 엔드포인트는 user-service 전용으로 별도 인증 필요
- **Flyway 미사용**: 이 프로젝트는 JPA `ddl-auto`로 스키마 관리 (`db/migration`은 참고용)
- **다중 인증 제공자**: LOCAL, KAKAO 지원 (향후 확장 가능)
- **로그인 히스토리**: LoginHistory 엔티티로 추적 가능 (현재 미사용)

## 개선 가능한 영역

1. **Token Refresh API**: Access Token 갱신 엔드포인트 추가
2. **로그아웃 구현**: Refresh Token 무효화 로직
3. **로그인 히스토리 활용**: IP, User-Agent 기반 이상 접근 감지
4. **OAuth2 통합**: KAKAO 외 다른 소셜 로그인 제공자 추가
5. **Rate Limiting**: 로그인 시도 횟수 제한으로 무차별 대입 공격 방어

---

**작성일**: 2026-01-25
**버전**: v1.0.0
**작성자**: Portfolio Project
