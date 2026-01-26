# User Service Guide

사용자 등록 및 프로필 관리를 담당하는 서비스입니다.

- **Port**: 8081
- **Database**: commerce-user (MariaDB)

## 핵심 기능

- 회원가입 (이메일, 비밀번호, 이름, 전화번호)
- 프로필 조회/수정
- 계정 삭제 (soft delete)
- 관리자 강제 수정/삭제

## Clean Architecture 계층

```
user-service/
├── api/                    # REST 컨트롤러, 요청 DTO
├── application/            # Use Cases, Commands, Ports
│   ├── usecase/            # RegisterUseCase, GetUserDetailUseCase 등
│   ├── port/               # AuthServiceClient (외부 포트)
│   └── repository/         # UserRepository
├── domain/                 # 엔티티, Value Objects
│   ├── entity/User.kt
│   └── vo/                 # Email, Phone VO
└── infra/                  # 구현체
    ├── persist/            # JPA Repository, Converters
    └── client/             # Feign Client (Auth Service 연동)
```

## Value Object 기반 도메인 검증

**Email VO**: 정규식 기반 이메일 형식 검증
```kotlin
@JvmInline
value class Email private constructor(val value: String) {
    companion object {
        fun of(rawEmail: String?): Email  // 검증 후 생성
    }
}
```

**Phone VO**: 한국 휴대폰 형식 (`010-XXXX-XXXX`)
```kotlin
@JvmInline
value class Phone(val value: String?)  // nullable, 형식 검증
```

**JPA Converter**: `@Converter(autoApply = true)`로 VO 자동 영속화

## 서비스 간 통신 (Auth Service 연동)

**Kafka 이벤트 없음** - Feign Client로 동기 통신

```kotlin
@FeignClient(name = "auth-service", url = "\${auth-service.url}")
interface AuthClient {
    @PostMapping("/api/auth")
    fun createUser(@RequestBody request: CreateUserRequest)
}
```

### 보상 트랜잭션 패턴

회원가입 시 User/Auth Service 간 데이터 일관성 보장:

```kotlin
@UseCase
class RegisterUseCase(...) {
    fun execute(command: CreateUserCommand) {
        // 1. User 저장
        val user = transactionRunner.run { registerUser(command) }

        // 2. Auth Service 호출
        try {
            authServiceClient.notifyUserCreated(...)
        } catch (ex: Exception) {
            // 3. 실패 시 보상 트랜잭션 (User 삭제)
            transactionRunner.runNew { deleteById(user.id!!) }
            throw ExternalServiceException(...)
        }
    }
}
```

## 도메인 모델

```kotlin
@Entity
class User(
    val email: Email,           // VO
    var name: String,
    var phone: Phone,           // VO
    var status: UserStatus,     // ACTIVE, INACTIVE, BLOCKED
    val role: UserRole,         // ROLE_USER, ROLE_ADMIN
    val provider: AuthProvider, // LOCAL, GOOGLE, KAKAO
)
```

## API 엔드포인트

| Method | Endpoint | 인증 | 설명 |
|--------|---------|------|------|
| POST | `/api/users` | X | 회원가입 |
| GET | `/api/users/{userId}` | X | 사용자 조회 |
| PATCH | `/api/users/me` | O | 본인 정보 수정 |
| DELETE | `/api/users/me` | O | 본인 계정 삭제 |
| PATCH | `/api/admin/users/{userId}` | O (ADMIN) | 강제 수정 |
| DELETE | `/api/admin/users/{userId}` | O (ADMIN) | 강제 삭제 |

## 환경 설정

```yaml
# application-local.yaml
auth-service:
  url: ${AUTH_SERVICE_URL:host.docker.internal:8089}
```

| 변수 | 기본값 | 설명 |
|------|--------|------|
| `DB_HOST` | localhost | MariaDB 호스트 |
| `AUTH_SERVICE_URL` | host.docker.internal:8089 | Auth Service URL |

## 에러 코드

| 코드 | 메시지 |
|------|--------|
| USER-400-001 | 비밀번호 형식 오류 |
| USER-400-002 | 이메일 형식 오류 |
| USER-404-001 | 사용자 없음 |
| USER-409-001 | 이메일 중복 |
| USER-409-003 | 이미 삭제된 사용자 |
