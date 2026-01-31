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

Email, Phone VO와 JPA Converter 사용 — 상세: `@services/user-service/.claude/docs/value-objects.md`

## 서비스 간 통신 (Auth Service 연동)

**Kafka 이벤트 없음** - Feign Client로 동기 통신, 보상 트랜잭션 패턴 적용

상세: `@services/user-service/.claude/docs/compensation-transaction.md`

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

## 에러 코드

상세: `@services/user-service/.claude/docs/error-codes.md`
