---
name: mono-new-service
description: 새로운 서비스 모듈 추가 가이드. 서비스 모듈 생성, 기본 구조 설정, settings.gradle.kts 업데이트가 필요할 때 사용합니다.
---

## 서비스 생성 순서

1. 디렉토리 구조 생성
2. build.gradle.kts 작성
3. Application.kt 작성
4. 설정 파일 작성 (application.yaml)
5. settings.gradle.kts에 모듈 추가

## Quick Reference

### 1. 디렉토리 구조

```
services/{service-name}/
├── build.gradle.kts
├── src/
│   ├── main/
│   │   ├── kotlin/
│   │   │   └── com/koosco/{servicename}/
│   │   │       ├── {ServiceName}Application.kt
│   │   │       ├── common/
│   │   │       │   └── config/
│   │   │       │       └── PublicEndpointProvider.kt
│   │   │       └── {domain}/
│   │   │           ├── api/
│   │   │           │   ├── controller/
│   │   │           │   ├── request/
│   │   │           │   └── response/
│   │   │           ├── application/
│   │   │           │   ├── usecase/
│   │   │           │   ├── command/
│   │   │           │   ├── result/
│   │   │           │   └── port/
│   │   │           ├── domain/
│   │   │           │   ├── entity/
│   │   │           │   ├── vo/
│   │   │           │   └── exception/
│   │   │           └── infra/
│   │   │               └── persist/
│   │   └── resources/
│   │       └── application.yaml
│   └── test/
│       └── kotlin/
└── Dockerfile
```

### 2. build.gradle.kts

```kotlin
plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("com.diffplug.spotless")
}

description = "{service-name}"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

dependencies {
    // kotlin
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // spring boot
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    testImplementation("org.springframework.security:spring-security-test")

    // common modules
    implementation(project(":common:common-core"))
    implementation(project(":common:common-security"))

    // jpa
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("org.mariadb.jdbc:mariadb-java-client")

    // swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.1")

    // logging
    implementation("net.logstash.logback:logstash-logback-encoder:7.4")

    // tracing
    implementation("io.micrometer:micrometer-tracing-bridge-brave")
    implementation("io.zipkin.reporter2:zipkin-reporter-brave")

    // kafka (필요시)
    implementation("org.springframework.kafka:spring-kafka")
    testImplementation("org.springframework.kafka:spring-kafka-test")

    // test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

spotless {
    kotlin {
        target("src/**/*.kt")
        ktlint("1.5.0")
            .editorConfigOverride(
                mapOf(
                    "max_line_length" to "120",
                    "indent_size" to "4",
                    "insert_final_newline" to "true",
                    "ktlint_standard_no-wildcard-imports" to "disabled",
                ),
            )
        trimTrailingWhitespace()
        endWithNewline()
    }
    kotlinGradle {
        target("*.gradle.kts")
        ktlint("1.5.0")
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

tasks.jar {
    enabled = false
}
```

### 3. Application.kt

```kotlin
package com.koosco.{servicename}

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class {ServiceName}Application

fun main(args: Array<String>) {
    runApplication<{ServiceName}Application>(*args)
}
```

### 4. PublicEndpointProvider.kt

JWT 인증을 사용하는 경우 공개 엔드포인트 설정:

```kotlin
package com.koosco.{servicename}.common.config

import com.koosco.commonsecurity.jwt.PublicEndpoints
import org.springframework.stereotype.Component

@Component
class PublicEndpointProvider : PublicEndpoints {
    override fun publicEndpoints(): List<String> {
        return listOf(
            "/actuator/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            // 추가 공개 엔드포인트
        )
    }
}
```

### 5. application.yaml

```yaml
spring:
  application:
    name: {service-name}
  datasource:
    url: jdbc:mariadb://localhost:3306/commerce-{service}
    username: admin
    password: admin1234
    driver-class-name: org.mariadb.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.MariaDBDialect

server:
  port: {PORT}

jwt:
  secret: ${JWT_SECRET:your-secret-key}
  expiration: 3600000

management:
  endpoints:
    web:
      exposure:
        include: health, prometheus
  endpoint:
    health:
      show-details: always
```

### 6. settings.gradle.kts 업데이트

```kotlin
// settings.gradle.kts (루트)
rootProject.name = "commerce"

// Common modules
include(":common:common-core")
include(":common:common-security")
include(":common:common-observability")

// Service modules
include(":services:auth-service")
include(":services:user-service")
include(":services:catalog-service")
include(":services:inventory-service")
include(":services:order-service")
include(":services:payment-service")
include(":services:{service-name}")  // ← 추가
```

### 7. Dockerfile

```dockerfile
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY build/libs/*.jar app.jar

ENV JAVA_OPTS=""

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

EXPOSE {PORT}
```

## 서비스 포트 규칙

| Service | Port |
|---------|------|
| auth-service | 8089 |
| user-service | 8081 |
| catalog-service | 8084 |
| inventory-service | 8083 |
| order-service | 8085 |
| payment-service | 8087 |

## 빌드 및 확인

```bash
# 모듈 추가 후 Gradle 동기화
./gradlew projects

# 새 서비스 빌드
./gradlew :services:{service-name}:build

# 실행
./gradlew :services:{service-name}:bootRun
```

## 체크리스트

- [ ] 디렉토리 구조 생성
- [ ] build.gradle.kts 작성
- [ ] Application.kt 작성
- [ ] PublicEndpointProvider.kt 작성 (JWT 사용시)
- [ ] application.yaml 작성
- [ ] settings.gradle.kts에 모듈 추가
- [ ] Dockerfile 작성
- [ ] 빌드 확인 (`./gradlew :services:{service-name}:build`)
