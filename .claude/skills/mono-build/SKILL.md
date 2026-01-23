---
name: mono-build
description: Gradle 멀티모듈 빌드 명령어 가이드. 빌드, 테스트, 포맷팅, 의존성 확인이 필요할 때 사용합니다.
---

## 사용 시점

- 전체/개별 모듈 빌드가 필요할 때
- 테스트 실행 또는 스킵이 필요할 때
- 코드 포맷팅(spotless)이 필요할 때
- 의존성 확인이 필요할 때

## Quick Reference

### 1. 전체 빌드

```bash
# 모든 모듈 빌드
./gradlew build

# 테스트 제외하고 빌드
./gradlew build -x test

# 클린 빌드
./gradlew clean build
```

### 2. 개별 모듈 빌드

```bash
# Common 모듈
./gradlew :common:common-core:build
./gradlew :common:common-security:build
./gradlew :common:common-observability:build

# Service 모듈
./gradlew :services:auth-service:build
./gradlew :services:user-service:build
./gradlew :services:catalog-service:build
./gradlew :services:inventory-service:build
./gradlew :services:order-service:build
./gradlew :services:payment-service:build
```

### 3. 테스트

```bash
# 전체 테스트 실행
./gradlew test

# 특정 모듈 테스트
./gradlew :services:order-service:test

# 특정 테스트 클래스 실행
./gradlew :services:order-service:test --tests "com.koosco.orderservice.order.OrderServiceTest"

# 테스트 리포트 확인
open build/reports/tests/test/index.html
```

### 4. 코드 포맷팅 (Spotless)

```bash
# 포맷 적용
./gradlew spotlessApply

# 포맷 검사만 (CI용)
./gradlew spotlessCheck

# 특정 모듈 포맷팅
./gradlew :services:order-service:spotlessApply
```

### 5. 의존성 확인

```bash
# 의존성 트리 출력
./gradlew :services:order-service:dependencies

# 특정 configuration의 의존성
./gradlew :services:order-service:dependencies --configuration runtimeClasspath

# 의존성 충돌 확인
./gradlew :services:order-service:dependencyInsight --dependency spring-boot
```

### 6. JAR 빌드 (Docker 이미지용)

```bash
# 실행 가능한 JAR 생성
./gradlew :services:auth-service:bootJar

# JAR 위치 확인
ls services/auth-service/build/libs/
```

### 7. 캐시 관리

```bash
# Gradle 캐시 정리
./gradlew cleanBuildCache

# 빌드 캐시 정리 후 빌드
./gradlew clean build --no-build-cache
```

## Gradle Wrapper

```bash
# Gradle 버전 확인
./gradlew --version

# Gradle Wrapper 업데이트
./gradlew wrapper --gradle-version=8.x.x
```

## 자주 사용하는 조합

```bash
# 개발 중 빠른 빌드 (테스트 스킵)
./gradlew build -x test

# 커밋 전 검증
./gradlew spotlessCheck test

# PR 전 전체 검증
./gradlew clean spotlessCheck build

# CI 파이프라인
./gradlew clean spotlessCheck build test
```

## 모듈 경로 규칙

| 모듈 유형 | 경로 패턴 | 예시 |
|----------|----------|------|
| Common | `:common:{module-name}` | `:common:common-core` |
| Service | `:services:{service-name}` | `:services:order-service` |
