# User Service

사용자 등록 및 프로필 관리를 담당하는 서비스입니다. Auth Service와의 동기적 연동을 통해 사용자 인증 정보를 관리하며, Value Object 기반 도메인 검증을 통해 데이터 무결성을 보장합니다.

## 목차

- [핵심 기능](#핵심-기능)
- [주요 기술적 성과](#주요-기술적-성과)
- [아키텍처](#아키텍처)
- [API 엔드포인트](#api-엔드포인트)
- [도메인 모델](#도메인-모델)
- [데이터 검증](#데이터-검증)
- [서비스 간 통신](#서비스-간-통신)
- [보안](#보안)
- [실행 방법](#실행-방법)

## 핵심 기능

### 사용자 관리
- **회원가입**: 이메일, 비밀번호, 이름, 전화번호를 통한 로컬 사용자 등록
- **프로필 조회**: 사용자 ID 기반 프로필 정보 조회
- **프로필 수정**: 이름, 전화번호 변경
- **계정 삭제**: 본인 계정 비활성화 (soft delete)

### 관리자 기능
- **강제 수정**: 모든 사용자 정보 강제 변경
- **강제 삭제**: 사용자 계정 강제 차단

## 주요 기술적 성과

### 1. Value Object 기반 도메인 검증

도메인 계층에서 데이터 무결성을 보장하기 위해 Value Object 패턴을 적용했습니다.

**Email Value Object**
```kotlin
@JvmInline
value class Email private constructor(val value: String) {
    init {
        require(isValid(value)) {
            "Invalid email format: $value"
        }
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

**Phone Value Object**
```kotlin
@JvmInline
value class Phone(val value: String?) {
    init {
        value?.let {
            require(it.isNotBlank()) { "Phone cannot be blank" }
            require(isValid(it)) { "Invalid phone format: $it" }
        }
    }

    companion object {
        private val phoneRegex = Regex("^010-\\d{3,4}-?\\d{4}$")
    }
}
```

**기술적 장점**:
- **타입 안전성**: 컴파일 타임에 이메일/전화번호 형식 검증
- **불변성**: `@JvmInline`으로 런타임 오버헤드 없이 타입 보장
- **도메인 규칙 캡슐화**: 검증 로직이 VO 내부에 응집

### 2. JPA AttributeConverter를 통한 VO 영속화

Value Object를 데이터베이스에 저장하기 위한 자동 변환 메커니즘을 구현했습니다.

```kotlin
@Converter(autoApply = true)
class EmailConverter : AttributeConverter<Email, String> {
    override fun convertToDatabaseColumn(attribute: Email?): String? =
        attribute?.value

    override fun convertToEntityAttribute(dbData: String?): Email? =
        dbData?.let { Email.of(it) }
}
```

**기술적 장점**:
- **자동 변환**: `autoApply = true`로 전역 적용
- **투명한 영속화**: 엔티티 코드에서 변환 로직 은닉
- **조회 시 검증**: DB에서 읽을 때도 VO 검증 규칙 적용

### 3. Spring Cloud OpenFeign을 통한 동기 통신

Auth Service와의 사용자 인증 정보 동기화를 위해 Feign Client를 사용했습니다.

**Feign Client 인터페이스**
```kotlin
@FeignClient(name = "auth-service", url = "\${auth-service.url}")
interface AuthClient {
    @PostMapping("/api/auth")
    fun createUser(@RequestBody request: CreateUserRequest)
}
```

**Adapter 구현**
```kotlin
@Component
class AuthClientAdapter(
    private val authClient: AuthClient,
    @Value("\${auth-service.url}")
    private val authServiceUrl: String,
) : AuthServiceClient {

    override fun notifyUserCreated(
        userId: Long,
        password: String,
        email: String,
        provider: AuthProvider?,
        role: UserRole,
    ) {
        try {
            authClient.createUser(
                CreateUserRequest(
                    userId = userId,
                    email = email,
                    password = password,
                    provider = provider,
                    role = role,
                )
            )
        } catch (e: FeignException) {
            throw ExternalServiceException(
                CommonErrorCode.EXTERNAL_SERVICE_ERROR,
                "Auth service 호출 실패: ${e.message}",
            )
        }
    }
}
```

**기술적 장점**:
- **선언적 HTTP 클라이언트**: 인터페이스만으로 REST 호출 정의
- **타입 안전성**: 컴파일 타임에 요청/응답 타입 검증
- **중앙화된 설정**: `application.yaml`에서 URL 관리

### 4. 분산 트랜잭션 보상(Compensating Transaction) 패턴

회원가입 시 User Service와 Auth Service 간의 데이터 일관성을 보장하기 위한 보상 트랜잭션을 구현했습니다.

```kotlin
@UseCase
class RegisterUseCase(
    private val userRepository: UserRepository,
    private val authServiceClient: AuthServiceClient,
    private val transactionRunner: TransactionRunner,
) {
    fun execute(command: CreateUserCommand) {
        // 1. User Service에 사용자 등록
        val user = transactionRunner.run { registerUser(command) }

        // 2. Auth Service에 인증 정보 등록
        try {
            authServiceClient.notifyUserCreated(
                userId = user.id!!,
                password = command.password,
                email = command.email,
                provider = command.provider,
                role = user.role,
            )
        } catch (ex: Exception) {
            // 3. Auth Service 실패 시 보상 트랜잭션 (rollback)
            runCatching {
                transactionRunner.runNew { deleteById(user.id!!) }
            }.onFailure {
                logger.error("rollback error", it)
            }
            throw ExternalServiceException(
                CommonErrorCode.EXTERNAL_SERVICE_ERROR,
                "Auth service 호출 실패로 회원가입 취소",
                ex,
            )
        }
    }
}
```

**시나리오별 처리**:

| 상황 | User Service | Auth Service | 결과 |
|------|-------------|-------------|------|
| 정상 케이스 | 저장 성공 | 저장 성공 | 회원가입 완료 |
| Auth 실패 | 저장 성공 → **삭제** | 저장 실패 | 회원가입 실패 (일관성 유지) |
| Rollback 실패 | 저장 유지 | 저장 실패 | 에러 로그 기록 (TODO: 추후 보상 로직 개선) |

**기술적 장점**:
- **Eventual Consistency**: 동기 호출 환경에서의 데이터 일관성 보장
- **명시적 보상**: Saga 패턴 없이 간단한 보상 트랜잭션 구현
- **에러 전파**: 최종 사용자에게 명확한 실패 사유 전달

**개선 가능 영역**:
- Rollback 실패 시 Dead Letter Queue 또는 Outbox 패턴 적용
- 재시도 메커니즘 추가 (Circuit Breaker, Retry with backoff)

### 5. Bean Validation과 커스텀 어노테이션

계층별 검증 전략을 적용하여 유효성 검사를 분리했습니다.

**API 레이어 검증**
```kotlin
data class RegisterRequest(
    @field:NotBlank(message = "이메일은 공백일 수 없습니다.")
    val email: String,

    @field:NotBlank(message = "비밀번호는 공백일 수 없습니다.")
    val password: String,

    @field:NotBlank(message = "이름은 공백일 수 없습니다.")
    val name: String,

    @field:NotBlankIfPresent(message = "전화번호는 공백일 수 없습니다.")
    val phone: String? = null,
)
```

**커스텀 어노테이션 (`@NotBlankIfPresent`)**
```kotlin
// common-core에서 제공
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [NotBlankIfPresentValidator::class])
annotation class NotBlankIfPresent(
    val message: String = "Field must not be blank if present"
)
```

**검증 레이어 분리**:
- **API 레이어**: Bean Validation으로 필수값, 형식 검증
- **Domain 레이어**: Value Object로 비즈니스 규칙 검증

### 6. Clean Architecture 기반 계층 분리

각 계층의 책임을 명확히 분리하여 유지보수성을 높였습니다.

```
user-service/
├── api/                    # API 레이어
│   ├── controller/         # REST 컨트롤러
│   │   ├── UserController.kt
│   │   └── AdminController.kt
│   └── Requests.kt         # 요청 DTO
│
├── application/            # Application 레이어
│   ├── usecase/            # 비즈니스 로직
│   │   ├── RegisterUseCase.kt
│   │   ├── GetUserDetailUseCase.kt
│   │   ├── UpdateMeUseCase.kt
│   │   ├── DeleteMeUseCase.kt
│   │   ├── ForceUpdateUseCase.kt
│   │   └── ForceDeleteUseCase.kt
│   ├── command/            # 입력 커맨드
│   ├── port/               # 외부 포트 인터페이스
│   │   └── AuthServiceClient.kt
│   └── repository/         # 레포지토리 인터페이스
│       └── UserRepository.kt
│
├── domain/                 # Domain 레이어
│   ├── entity/
│   │   └── User.kt
│   ├── vo/                 # Value Objects
│   │   ├── Email.kt
│   │   └── Phone.kt
│   └── enums/
│       ├── UserStatus.kt   # ACTIVE, INACTIVE, BLOCKED
│       ├── UserRole.kt     # ROLE_USER, ROLE_ADMIN
│       └── AuthProvider.kt # LOCAL, GOOGLE, KAKAO
│
└── infra/                  # Infrastructure 레이어
    ├── persist/            # 영속성 구현
    │   ├── JpaUserRepository.kt
    │   ├── UserRepositoryImpl.kt
    │   ├── UserQuery.kt    # QueryDSL 쿼리
    │   └── converter/      # JPA Converters
    │       ├── EmailConverter.kt
    │       └── PhoneConverter.kt
    └── client/             # 외부 통신 구현
        ├── AuthClient.kt   # Feign Client
        └── AuthClientAdapter.kt
```

**의존성 규칙**:
- `api` → `application` (컨트롤러가 Use Case 호출)
- `application` → `domain` (Use Case가 도메인 엔티티 사용)
- `infra` → `application`, `domain` (구현체가 인터페이스 구현)
- `domain`은 어디에도 의존하지 않음 (순수 도메인 로직)

## 아키텍처

### 서비스 간 통신 흐름

```
[사용자]
   │
   │ POST /api/users (회원가입)
   ▼
[User Service]
   │
   │ 1. User 엔티티 생성 및 저장
   │    (Email, Phone VO 검증)
   │
   │ 2. Feign Client 호출
   ▼
[Auth Service]
   │
   │ POST /api/auth
   │ (userId, password, email, role 전달)
   │
   │ 3. 인증 정보 저장
   │
   ▼
[응답]
   │
   │ 성공 시: 회원가입 완료
   │ 실패 시: User 삭제 (보상 트랜잭션) → 실패 응답
```

### 데이터베이스 스키마

```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(20),
    status VARCHAR(20) NOT NULL,      -- ACTIVE, INACTIVE, BLOCKED
    role VARCHAR(20) NOT NULL,        -- ROLE_USER, ROLE_ADMIN
    provider VARCHAR(20) NOT NULL,    -- LOCAL, GOOGLE, KAKAO
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    INDEX idx_email (email),
    INDEX idx_status (status)
);
```

## API 엔드포인트

### 사용자 API

| Method | Endpoint | 인증 | 설명 |
|--------|---------|------|------|
| POST | `/api/users` | 불필요 | 회원가입 |
| GET | `/api/users/{userId}` | 불필요 | 사용자 조회 |
| PATCH | `/api/users/me` | **필요** | 본인 정보 수정 |
| DELETE | `/api/users/me` | **필요** | 본인 계정 삭제 |

### 관리자 API

| Method | Endpoint | 인증 | 권한 | 설명 |
|--------|---------|------|------|------|
| PATCH | `/api/admin/users/{userId}` | **필요** | ADMIN | 사용자 강제 수정 |
| DELETE | `/api/admin/users/{userId}` | **필요** | ADMIN | 사용자 강제 삭제 |

### 요청/응답 예시

**회원가입 (POST /api/users)**
```json
// Request
{
  "email": "user@example.com",
  "password": "securePassword123!",
  "name": "홍길동",
  "phone": "010-1234-5678",
  "provider": "LOCAL"
}

// Response (성공)
{
  "success": true,
  "data": null,
  "error": null
}

// Response (실패 - 중복 이메일)
{
  "success": false,
  "data": null,
  "error": {
    "code": "USER-409-001",
    "message": "이미 사용 중인 이메일입니다.",
    "timestamp": "2026-01-25T10:30:00"
  }
}
```

**프로필 수정 (PATCH /api/users/me)**
```json
// Request
{
  "name": "김철수",
  "phone": "010-9876-5432"
}

// Response
{
  "success": true,
  "data": null,
  "error": null
}
```

## 도메인 모델

### User 엔티티

```kotlin
@Entity
@Table(name = "users")
class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    val email: Email,                   // Value Object
    var name: String,
    var phone: Phone,                   // Value Object

    @Enumerated(EnumType.STRING)
    var status: UserStatus = UserStatus.ACTIVE,

    @Enumerated(EnumType.STRING)
    val role: UserRole = UserRole.ROLE_USER,

    @Enumerated(EnumType.STRING)
    val provider: AuthProvider = AuthProvider.LOCAL,

    val createdAt: LocalDateTime = LocalDateTime.now(),
    var updatedAt: LocalDateTime = LocalDateTime.now(),
) {
    fun update(name: String?, phone: Phone?) {
        this.name = name ?: this.name
        this.phone = phone ?: this.phone
        this.updatedAt = LocalDateTime.now()
    }

    fun quit() {
        this.status = UserStatus.INACTIVE
        this.updatedAt = LocalDateTime.now()
    }

    fun forceDelete() {
        if (status === UserStatus.BLOCKED) {
            throw ConflictException(UserErrorCode.USER_ALREADY_DELETED)
        }
        this.status = UserStatus.BLOCKED
        this.updatedAt = LocalDateTime.now()
    }
}
```

### 도메인 규칙

1. **이메일**: 정규식 검증 (RFC 5322 기반 간소화 버전)
2. **전화번호**: 한국 휴대폰 형식 (`010-XXXX-XXXX`)
3. **사용자 상태**:
   - `ACTIVE`: 활성 사용자
   - `INACTIVE`: 본인이 탈퇴한 사용자 (재활성화 가능)
   - `BLOCKED`: 관리자가 강제 삭제한 사용자 (복구 불가)

## 데이터 검증

### 검증 계층

| 계층 | 검증 방법 | 검증 대상 |
|------|----------|----------|
| API | Bean Validation (`@NotBlank`, `@NotBlankIfPresent`) | 필수값, null 체크 |
| Application | Use Case 로직 | 비즈니스 규칙 (중복 체크, 상태 검증) |
| Domain | Value Object | 도메인 규칙 (이메일 형식, 전화번호 형식) |

### 에러 코드

```kotlin
enum class UserErrorCode(
    override val code: String,
    override val message: String,
    override val status: HttpStatus
) : ErrorCode {
    // 400 Bad Request
    INVALID_PASSWORD_FORMAT("USER-400-001", "비밀번호 형식이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL_FORMAT("USER-400-002", "이메일 형식이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    PASSWORD_MISMATCH("USER-400-003", "비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),

    // 401 Unauthorized
    INVALID_CREDENTIALS("USER-401-001", "아이디 또는 비밀번호가 올바르지 않습니다.", HttpStatus.UNAUTHORIZED),

    // 404 Not Found
    USER_NOT_FOUND("USER-404-001", "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    // 409 Conflict
    EMAIL_ALREADY_EXISTS("USER-409-001", "이미 사용 중인 이메일입니다.", HttpStatus.CONFLICT),
    USERNAME_ALREADY_EXISTS("USER-409-002", "이미 사용 중인 사용자명입니다.", HttpStatus.CONFLICT),
    USER_ALREADY_DELETED("USER-409-003", "이미 삭제된 사용자입니다.", HttpStatus.CONFLICT),
}
```

## 서비스 간 통신

### Auth Service 연동

**목적**: User Service에서 생성한 사용자의 인증 정보를 Auth Service에 등록

**통신 방식**: Spring Cloud OpenFeign (동기 HTTP)

**설정** (`application-local.yaml`)
```yaml
auth-service:
  url: ${AUTH_SERVICE_URL:host.docker.internal:8089}
```

**호출 시퀀스**:
1. `RegisterUseCase.execute()`에서 사용자 생성
2. `AuthServiceClient.notifyUserCreated()` 호출
3. Auth Service에서 `userId`, `email`, `password`, `role` 저장
4. 실패 시 User 삭제 후 예외 발생

**장점**:
- 간단한 동기 호출로 빠른 피드백
- 사용자에게 즉시 결과 전달

**단점**:
- Auth Service 장애 시 회원가입 불가
- 네트워크 지연 시 응답 시간 증가

**향후 개선 방향**:
- Kafka Event 기반 비동기 처리로 전환 검토
- Circuit Breaker 패턴 적용 (Resilience4j)

## 보안

### 1. JWT 인증

**인증 필터**: `common-security` 모듈의 `JwtAuthenticationFilter`가 자동 적용

**인증이 필요 없는 엔드포인트** (`PublicEndpoints`)
```kotlin
@Component
class PublicEndpoints : PublicEndpointProvider {
    override fun publicEndpoints(): Array<String> = arrayOf(
        "/api/users",           // 회원가입
        "/api/users/login",     // 로그인 (실제 로그인은 Auth Service에서 처리)
        "/api/users/**",        // 사용자 조회
    )
}
```

**인증된 사용자 ID 주입**: `@AuthId` 어노테이션 사용
```kotlin
@PatchMapping("/me")
fun updateMe(@AuthId userId: Long, @RequestBody request: UpdateRequest): ApiResponse<Any> {
    updateMeUseCase.execute(request.toCommand(userId))
    return ApiResponse.success()
}
```

### 2. 비밀번호 보안

- User Service는 **평문 비밀번호를 저장하지 않음**
- 회원가입 시 Auth Service로 전달 후 Auth Service에서 암호화
- User Service는 오직 사용자 프로필 정보만 관리

### 3. SQL Injection 방지

- JPA/QueryDSL 사용으로 자동 파라미터 바인딩
- Native Query 사용 안 함

## 실행 방법

### 로컬 환경

**1. 사전 준비**
```bash
# 데이터베이스 실행 (infra/docker)
cd /Users/koo/CodeSpace/commerce/mono/infra/docker
docker-compose up -d mariadb

# Auth Service 실행 (8089 포트)
cd /Users/koo/CodeSpace/commerce/mono
./gradlew :services:auth-service:bootRun
```

**2. User Service 실행**
```bash
# 빌드
./gradlew :services:user-service:build

# 실행 (8081 포트)
./gradlew :services:user-service:bootRun

# 또는 JAR 실행
java -jar services/user-service/build/libs/user-service-0.0.1-SNAPSHOT.jar
```

**3. API 테스트**
```bash
# Swagger UI 접속
open http://localhost:8081/swagger-ui.html

# 회원가입 테스트
curl -X POST http://localhost:8081/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123!",
    "name": "테스트유저",
    "phone": "010-1234-5678"
  }'
```

### Docker 실행

```bash
# 이미지 빌드
cd services/user-service
docker build -t user-service:latest .

# 컨테이너 실행
docker run -p 8081:8081 \
  -e DB_HOST=host.docker.internal \
  -e AUTH_SERVICE_URL=http://host.docker.internal:8089 \
  user-service:latest
```

### Kubernetes 배포

```bash
# k8s 리소스 적용 (infra/k8s)
cd /Users/koo/CodeSpace/commerce/mono/infra
make k8s-apply-all ENV=dev

# 서비스 시작
make k8s-start

# 포트 포워딩
kubectl port-forward -n commerce svc/user-service 8081:8081
```

## 환경 변수

| 변수명 | 기본값 | 설명 |
|--------|--------|------|
| `DB_HOST` | localhost | MariaDB 호스트 |
| `DB_PORT` | 3306 | MariaDB 포트 |
| `DB_NAME` | commerce-user | 데이터베이스 이름 |
| `DB_USERNAME` | admin | DB 사용자명 |
| `DB_PASSWORD` | admin1234 | DB 비밀번호 |
| `AUTH_SERVICE_URL` | host.docker.internal:8089 | Auth Service URL |
| `JWT_SECRET` | (256bit 이상 문자열) | JWT 서명 키 |
| `JWT_EXPIRATION` | 86400 | JWT 만료 시간 (초) |

## 모니터링

### Actuator 엔드포인트

```bash
# Health Check
curl http://localhost:8081/actuator/health

# Prometheus Metrics
curl http://localhost:8081/actuator/prometheus
```

### Grafana 대시보드

- **URL**: http://localhost:3000 (infra/monitoring)
- **계정**: admin / admin123
- **대시보드**: JVM 메트릭, HTTP 요청 통계, DB 커넥션 풀

## 개발 가이드

### 코드 포맷팅

```bash
# 포맷 검사
./gradlew :services:user-service:spotlessCheck

# 자동 포맷팅
./gradlew :services:user-service:spotlessApply
```

### 테스트 실행

```bash
# 전체 테스트
./gradlew :services:user-service:test

# 특정 테스트
./gradlew :services:user-service:test --tests "UserServiceApplicationTests"
```

## 참고 자료

- [Common Core 가이드](/Users/koo/CodeSpace/commerce/mono/common/common-core/docs/getting-started.md)
- [Exception Handling 가이드](/Users/koo/CodeSpace/commerce/mono/common/common-core/docs/exception-handling.md)
- [API Response 가이드](/Users/koo/CodeSpace/commerce/mono/common/common-core/docs/api-response.md)
- [Clean Architecture 패턴](./.claude/skills/mono-clean-arch.md)

## 포트폴리오 요약

**User Service**는 다음과 같은 기술적 역량을 보여줍니다:

1. **도메인 주도 설계**: Value Object로 도메인 규칙 캡슐화
2. **Clean Architecture**: 계층별 책임 분리 및 의존성 규칙 준수
3. **분산 시스템 트랜잭션**: 보상 트랜잭션 패턴으로 데이터 일관성 보장
4. **서비스 간 통신**: Spring Cloud OpenFeign으로 동기 통신 구현
5. **데이터 검증**: Bean Validation + Value Object 다층 검증
6. **보안**: JWT 인증, 역할 기반 접근 제어 (RBAC)
7. **관측성**: Actuator, Prometheus 메트릭 노출
