# User Service Guide

사용자 등록, 프로필 관리, 인증(JWT 발급/로그인)을 담당하는 서비스입니다.

- **Port**: 8081
- **Database**: commerce-user (MariaDB)
- **Cache**: Redis (refresh token 저장)

## 핵심 기능

- 회원가입 (이메일, 비밀번호, 이름, 전화번호)
- 로그인 / 로그아웃 (JWT Access Token + Refresh Token)
- 토큰 갱신 (Refresh Token → 새 토큰 발급)
- 프로필 조회/수정
- 계정 탈퇴 (soft delete, WITHDRAWN)
- 관리자 강제 수정/잠금 (LOCKED)

## Clean Architecture 계층

```
user-service/
├── api/                    # REST 컨트롤러, 요청 DTO
├── application/            # Use Cases, Commands, Ports
│   ├── usecase/            # Register, Login, Refresh, Logout 등
│   ├── port/               # UserRepository, TokenGeneratorPort, RefreshTokenStorePort
│   ├── command/            # CreateUserCommand, LoginCommand 등
│   └── dto/                # UserDto, AuthTokenDto
├── domain/                 # 엔티티, Value Objects, Enums
│   ├── entity/             # Member, OAuthAccount, Address
│   ├── enums/              # MemberStatus, MemberRole, OAuthProvider
│   └── vo/                 # Email, Phone, EncryptedPassword
└── infra/                  # 구현체
    ├── persist/            # JPA Repository, QueryDSL, Converters
    ├── security/           # JwtTokenGeneratorAdapter
    ├── redis/              # RedisRefreshTokenAdapter
    └── config/             # SecurityBeanConfig, RedisConfig, PublicEndpoints
```

## Value Object 기반 도메인 검증

Email, Phone, EncryptedPassword VO와 JPA Converter 사용

## 도메인 모델

```kotlin
@Entity @Table(name = "member_user")
class Member(
    val email: Email,                   // VO
    var name: String,
    var phone: Phone?,                  // VO, nullable
    var passwordHash: EncryptedPassword?, // VO, nullable (소셜 전용 사용자)
    val role: MemberRole,               // USER, ADMIN
    var status: MemberStatus,           // ACTIVE, DORMANT, LOCKED, WITHDRAWN
)
```

## API 엔드포인트

| Method | Endpoint | 인증 | 설명 |
|--------|---------|------|------|
| POST | `/api/users` | X | 회원가입 |
| GET | `/api/users/{userId}` | X | 사용자 조회 |
| PATCH | `/api/users/me` | O | 본인 정보 수정 |
| DELETE | `/api/users/me` | O | 본인 계정 탈퇴 |
| PATCH | `/api/users/{userId}` | O (ADMIN) | 강제 수정 |
| DELETE | `/api/users/{userId}` | O (ADMIN) | 강제 잠금 |
| POST | `/api/auth/login` | X | 로그인 |
| POST | `/api/auth/refresh` | X | 토큰 갱신 |
| POST | `/api/auth/logout` | O | 로그아웃 |

## 인증 플로우

- **로그인**: Member 조회 → BCrypt 검증 → JWT 발급 → Redis에 Refresh Token 저장
- **토큰 갱신**: Cookie에서 Refresh Token 추출 → Redis 검증 → 새 토큰 발급
- **로그아웃**: Redis에서 Refresh Token 삭제
- **Refresh Token**: Redis key `member:refresh-token:{userId}`, TTL 7일

## 에러 코드

`MemberErrorCode` 참조 — MEMBER-4xx-xxx 형식
