# Spring Boot 서비스 Dockerfile 점검과 최적화

## 문제 정의

6개의 Spring Boot 서비스를 k3s 클러스터에 배포하고 있습니다. 모든 서비스가 동일한 Dockerfile 템플릿을 사용하고 있었는데, Smoke Test 이슈를 해결하면서 배포 파이프라인을 점검하게 되었습니다.

기존 Dockerfile은 다음과 같습니다.

```dockerfile
FROM eclipse-temurin:21-jre-alpine

RUN addgroup -S spring && adduser -S spring -G spring
WORKDIR /app
COPY build/libs/*.jar app.jar
RUN chown -R spring:spring /app
USER spring:spring

HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health/liveness || exit 1

EXPOSE 8080

ENV JAVA_OPTS="-XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=75.0 \
               -XX:InitialRAMPercentage=50.0 \
               -XX:+UseG1GC \
               -XX:+DisableExplicitGC \
               -Djava.security.egd=file:/dev/./urandom"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

동작에는 문제가 없었지만, 몇 가지 비효율과 잠재적 문제가 있었습니다.

**SIGTERM이 Java 프로세스에 전달되지 않습니다.** `sh -c`로 감싸면 shell이 PID 1이 됩니다. k8s가 Pod 종료 시 SIGTERM을 보내면 shell이 받고, Java 프로세스에는 전달되지 않습니다. 결과적으로 Spring Boot의 graceful shutdown이 동작하지 않고, `terminationGracePeriodSeconds`(기본 30초) 후 SIGKILL로 강제 종료됩니다.

**`COPY`와 `chown`이 불필요하게 두 레이어를 차지합니다.** `COPY build/libs/*.jar app.jar` 이후 `RUN chown -R spring:spring /app`이 별도 레이어로 실행됩니다. JAR 파일이 변경될 때마다 두 레이어 모두 무효화됩니다. `COPY --chown` 옵션을 사용하면 한 레이어로 합칠 수 있습니다.

**Docker 레벨 HEALTHCHECK가 k8s 프로브와 중복됩니다.** k8s Deployment에 livenessProbe, readinessProbe, startupProbe가 이미 설정되어 있으므로, Docker의 HEALTHCHECK는 불필요합니다. `docker run`으로 단독 실행할 때만 의미가 있는 설정입니다.

**`*.jar` 글롭 패턴이 잘못된 JAR을 복사할 수 있습니다.** Spring Boot의 Gradle 빌드는 기본적으로 실행 가능한 fat jar와 plain jar(의존성 미포함) 두 개를 생성합니다. `COPY build/libs/*.jar app.jar`이 plain jar를 선택하면 앱이 시작되지 않습니다.

**`.dockerignore`가 없습니다.** Docker 빌드 시 빌드 컨텍스트에 `.git`, `.gradle`, `src`, `build/classes` 등 불필요한 파일이 모두 포함됩니다. `COPY` 명령에서 실제로 사용하는 것은 `build/libs/*.jar` 하나뿐인데, Docker daemon에 전송되는 컨텍스트가 필요 이상으로 커집니다.

## 대안책

### 1. Docker Multi-stage Build 도입

Dockerfile 안에서 Gradle 빌드를 수행하고, 빌드 결과물만 런타임 이미지로 복사하는 방법입니다.

```dockerfile
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /build
COPY gradle/ gradle/
COPY gradlew settings.gradle.kts build.gradle.kts ./
COPY common/ common/
COPY services/auth-service/ services/auth-service/
RUN ./gradlew :services:auth-service:build -x test

FROM eclipse-temurin:21-jre-alpine
COPY --from=builder /build/services/auth-service/build/libs/*.jar app.jar
```

CI/CD 환경에서는 재현 가능한 빌드를 보장하므로 이상적입니다. 하지만 mono repo 구조에서는 치명적인 단점이 있습니다.

- `common` 모듈 한 줄 수정 시 6개 서비스 전체의 의존성 레이어 캐시가 무효화됩니다
- 서비스마다 Gradle daemon이 cold start하므로 호스트 빌드 대비 수 배 느립니다
- 빌드 컨텍스트가 프로젝트 루트 전체여야 하므로 `.dockerignore` 관리가 복잡해집니다
- 호스트의 Gradle build cache(`~/.gradle/caches`)를 활용할 수 없습니다

### 2. 현재 방식 유지 + Dockerfile 개선 (선택)

호스트에서 `./gradlew build`로 빌드한 뒤 JAR만 Docker 이미지에 복사하는 기존 흐름을 유지하되, Dockerfile의 비효율을 개선하는 방법입니다.

이 방법을 선택한 이유는 다음과 같습니다.

- **Gradle incremental build가 Docker layer caching보다 효율적입니다.** Gradle은 소스 파일 단위로 변경을 추적하고, 변경된 모듈만 재컴파일합니다. Docker layer caching은 `COPY` 명령의 파일 해시가 바뀌면 해당 레이어부터 전부 무효화됩니다. mono repo에서 공유 모듈이 있으면 layer caching의 이점이 크게 줄어듭니다
- **빌드 속도가 빠릅니다.** 호스트에서 전체 빌드 후 Docker 이미지 생성은 서비스당 2초 미만입니다. Multi-stage는 서비스당 수 분이 걸립니다
- **현재 배포 환경에 맞습니다.** 로컬 k3s에 `k3d image import`로 이미지를 전달하는 구조이므로, 빌드 재현성보다 빌드 속도가 더 중요합니다

## 해결 과정

### PID 1 문제 해결: `exec` 추가

`sh -c` 안에서 `exec`를 사용하면 shell이 Java 프로세스로 대체됩니다. `exec`는 현재 shell 프로세스를 지정된 명령으로 교체하는 POSIX 기능입니다. fork + exec가 아닌 exec만 수행하므로, Java 프로세스가 PID 1이 됩니다.

```dockerfile
# 변경 전 - shell이 PID 1
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

# 변경 후 - java가 PID 1
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
```

이 차이가 중요한 이유는 k8s의 Pod 종료 과정 때문입니다.

1. k8s가 SIGTERM을 PID 1에 전송합니다
2. `terminationGracePeriodSeconds` (기본 30초) 동안 기다립니다
3. 아직 종료되지 않으면 SIGKILL을 전송합니다

`exec` 없이는 SIGTERM이 shell에 도달하지만 Java에는 전달되지 않습니다. Spring Boot의 graceful shutdown(`server.shutdown=graceful`)이 동작하지 않으므로, 진행 중인 요청이 갑자기 끊길 수 있습니다.

`exec`를 추가하면 Java가 SIGTERM을 직접 받아 graceful shutdown을 수행합니다. 진행 중인 HTTP 요청 처리를 완료한 뒤 ApplicationContext를 정리하고 종료합니다.

`ENTRYPOINT`를 exec form으로 바꾸면 `sh -c` 없이도 가능하지만, `$JAVA_OPTS` 환경 변수 확장이 필요하므로 shell을 거쳐야 합니다. k8s Deployment에서 `JAVA_OPTS`를 env로 오버라이드하는 패턴을 사용하고 있으므로 이 구조를 유지합니다.

### 레이어 최적화: `COPY --chown`

```dockerfile
# 변경 전 - 2 레이어
COPY build/libs/*.jar app.jar
RUN chown -R spring:spring /app

# 변경 후 - 1 레이어
COPY --chown=spring:spring build/libs/*.jar app.jar
```

`COPY --chown`은 파일 복사와 소유권 변경을 한 레이어에서 수행합니다. 별도의 `RUN chown`은 JAR 파일을 다시 읽어서 새 레이어로 복사하는 작업이므로, 이미지 크기가 JAR 사이즈만큼 불필요하게 증가합니다.

### Docker HEALTHCHECK 제거

```dockerfile
# 제거
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health/liveness || exit 1
```

k8s 환경에서는 startupProbe, livenessProbe, readinessProbe가 헬스 체크를 전담합니다. Docker HEALTHCHECK는 `docker run`으로 단독 실행할 때만 의미가 있고, k8s에서는 무시됩니다. 오히려 불필요한 `wget` 프로세스가 주기적으로 실행되어 리소스를 소비할 수 있습니다.

### Plain JAR 생성 방지

6개 서비스 중 4개(auth, catalog, order, inventory)는 이미 `tasks.jar { enabled = false }`가 설정되어 있었지만, user-service와 payment-service에는 없었습니다. 이 두 서비스의 `build.gradle.kts`에 설정을 추가합니다.

```kotlin
tasks.jar {
    enabled = false
}
```

이 설정이 없으면 Gradle은 두 종류의 JAR을 생성합니다.

```
build/libs/
├── user-service-0.0.1-SNAPSHOT.jar         # fat jar (실행 가능, ~80MB)
└── user-service-0.0.1-SNAPSHOT-plain.jar   # plain jar (의존성 미포함, ~1MB)
```

`COPY build/libs/*.jar app.jar`에서 `*.jar`이 두 파일을 모두 매칭하면 예측 불가능한 결과가 발생합니다. `tasks.jar { enabled = false }`로 plain jar 생성을 차단하면 `build/libs/`에 fat jar만 남으므로 글롭 패턴이 안전해집니다.

### `.dockerignore` 추가

프로젝트 루트에 `.dockerignore`를 생성합니다.

```
.git
.gradle
.idea
.claude
**/src
**/build/classes
**/build/generated
**/build/reports
**/build/test-results
**/build/tmp
**/logs
docs
load-test
infra
*.md
```

핵심은 `**/src`와 `**/build/classes` 등 소스 코드와 빌드 중간 산물을 제외하는 것입니다. Docker 이미지에 필요한 것은 `build/libs/*.jar` 하나뿐이므로, 나머지는 모두 빌드 컨텍스트에서 제외합니다.

### 최종 Dockerfile

```dockerfile
FROM eclipse-temurin:21-jre-alpine

RUN addgroup -S spring && adduser -S spring -G spring

WORKDIR /app

COPY --chown=spring:spring build/libs/*.jar app.jar

USER spring:spring

EXPOSE 8080

ENV JAVA_OPTS="-XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=75.0 \
               -XX:InitialRAMPercentage=50.0 \
               -XX:+UseG1GC \
               -XX:+DisableExplicitGC \
               -Djava.security.egd=file:/dev/./urandom"

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
```

기존 35줄에서 17줄로 줄었지만, 기능적으로는 더 정확해졌습니다.

## 결과

변경된 파일은 다음과 같습니다.

| 파일 | 변경 내용 |
|------|----------|
| `services/*/Dockerfile` (6개) | `exec` 추가, `COPY --chown`, HEALTHCHECK 제거 |
| `.dockerignore` (신규) | 빌드 컨텍스트 최적화 |
| `services/user-service/build.gradle.kts` | `tasks.jar { enabled = false }` |
| `services/payment-service/build.gradle.kts` | `tasks.jar { enabled = false }` |

이번 작업에서 얻은 인사이트는 다음과 같습니다.

- **`exec`는 컨테이너 환경에서 필수입니다.** Docker와 k8s는 PID 1에 시그널을 보냅니다. shell wrapper를 사용할 때 `exec`를 빠뜨리면 graceful shutdown이 동작하지 않습니다. 이 문제는 평소에는 드러나지 않고, 배포 중 요청이 끊기는 증상으로 나타나기 때문에 원인을 찾기 어렵습니다
- **Docker multi-stage build가 항상 정답은 아닙니다.** mono repo에서 공유 모듈이 있으면 Docker layer caching의 이점이 크게 줄어듭니다. Gradle의 incremental build와 build cache가 이미 잘 동작하고 있다면, 호스트 빌드 + JAR 복사 방식이 더 효율적일 수 있습니다. Multi-stage build는 CI/CD에서 클린 환경 빌드가 필요할 때 도입을 검토하면 됩니다
- **k8s 환경에서 Docker HEALTHCHECK는 불필요합니다.** k8s probe와 Docker HEALTHCHECK는 동일한 역할을 하지만 독립적으로 동작합니다. k8s가 Pod 상태를 관리하는 환경에서 Docker HEALTHCHECK를 남겨두면, 불필요한 프로세스가 주기적으로 실행될 뿐 실질적인 이점이 없습니다
- **Gradle의 plain jar 설정은 서비스 추가 시 빠뜨리기 쉽습니다.** `tasks.jar { enabled = false }`가 없으면 `build/libs/`에 두 개의 JAR이 생성됩니다. 서비스 템플릿이나 체크리스트에 이 설정을 포함해야 합니다
