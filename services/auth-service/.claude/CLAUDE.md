# Auth Service Guide

## 개요

JWT 기반 인증 및 토큰 발급 서비스입니다.

- **포트**: 8089
- **데이터베이스**: commerce-auth (MariaDB)
- **주요 책임**: JWT 토큰 발급, 사용자 인증 정보 관리
- **Kafka 이벤트**: 없음 (순수 CRUD 서비스)

## Clean Architecture 구조

```
auth-service/
├── api/              # REST 컨트롤러, 요청/응답 DTO
├── application/      # UseCase (LoginUseCase, RegisterUseCase), Port
├── domain/           # Entity (UserAuth), VO (Email, EncryptedPassword)
└── infra/            # JPA 리포지토리, JWT 토큰 생성기
```

**의존성 규칙**: application/domain -> api/infra 의존 금지

## 핵심 기능

### JWT 토큰 발급
- **Access Token**: 24시간 유효, API 요청 인증용
- **Refresh Token**: 7일 유효, DB 저장, HttpOnly 쿠키 전달

### 비밀번호 암호화
- BCrypt 알고리즘 사용
- 솔트 자동 생성, 동일 비밀번호도 매번 다른 해시값

### 인증 플로우
1. user-service에서 회원가입 시 auth-service로 인증 정보 등록 (내부 API)
2. 로그인 시 이메일/비밀번호 검증 후 JWT 발급
3. 다른 서비스는 common-security의 JWT 필터로 토큰 검증

## 도메인 모델

| 모델 | 설명 |
|-----|------|
| `UserAuth` | 사용자 인증 엔티티 (userId, email, password, provider, role) |
| `Email` | Value Object - 정규식 기반 형식 검증 |
| `EncryptedPassword` | Value Object - 암호화된 비밀번호 타입 구분 |
| `AuthProvider` | LOCAL, KAKAO 등 인증 제공자 열거형 |

## 보안 특성

- **XSS 방어**: Refresh Token은 HttpOnly 쿠키로 전달
- **CSRF 방어**: SameSite=Strict 설정
- **정보 노출 방지**: 인증 실패 시 동일한 에러 메시지 반환
- **SecretKey**: 256비트(32바이트) 이상 필수

## API 요약

| 엔드포인트 | 설명 | 비고 |
|-----------|------|------|
| `POST /api/auth` | 인증 정보 등록 | user-service 전용 내부 API |
| `POST /api/auth/login` | 로그인 및 토큰 발급 | Access Token (Header), Refresh Token (Cookie) |

## 환경 설정

| 변수 | 기본값 | 설명 |
|-----|-------|------|
| `JWT_SECRET` | - | JWT 서명 키 (256비트 이상) |
| `JWT_EXPIRATION` | 86400 | Access Token 만료 (초) |
| `JWT_REFRESH_EXPIRATION` | 604800 | Refresh Token 만료 (초) |
| `DB_HOST` | localhost | MariaDB 호스트 |
| `DB_PORT` | 3309 | MariaDB 포트 |

## 빌드 명령어

```bash
./gradlew :services:auth-service:build    # 빌드
./gradlew :services:auth-service:test     # 테스트
./gradlew :services:auth-service:bootRun  # 실행
```

## 참고 사항

- Flyway 미사용 (JPA ddl-auto로 스키마 관리)
- common-security 모듈이 JWT 검증 필터 자동 설정
- 다중 인증 제공자 지원 (LOCAL, KAKAO)
