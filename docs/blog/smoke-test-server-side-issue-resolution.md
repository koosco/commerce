# Smoke Test 서버 측 이슈 3건을 추적하고 해결한 과정

## 문제 정의

k6 Smoke Test를 prod 환경에서 실행한 결과, 6개 서비스 중 3개에서 실패가 발생했습니다.

| 이슈 | 증상 | 심각도 |
|------|------|--------|
| Auth Service 간헐적 실패 | 1-2 VU에서 성공률 64% | 높음 |
| Inventory Service 500 | 모든 요청에서 500 Internal Server Error | 높음 |
| Catalog Service 401 | GET 요청에 인증 없이 접근 시 401 Unauthorized | 중간 |

Smoke Test는 1-2 VU(Virtual User)로 기본적인 엔드포인트 동작만 확인하는 테스트입니다. 이 수준에서 실패한다면 커넥션 풀이나 동시성 문제가 아닌, 서비스 자체의 가용성 또는 설정 문제를 의미합니다. 부하 테스트로 넘어가기 전에 반드시 해결해야 하는 이슈들이었습니다.

## 대안책

세 이슈의 원인이 각각 다를 것으로 예상했으므로, 개별적으로 원인을 분석하고 적절한 해결 방법을 선택했습니다.

### Auth Service: startupProbe 추가 vs livenessProbe 조정

Auth Service의 64% 성공률은 Pod가 간헐적으로 재시작되고 있음을 시사합니다. k8s에서 이런 패턴이 나타나는 대표적인 원인은 livenessProbe 실패로 인한 재시작 루프입니다.

기존 설정은 `initialDelaySeconds: 60` + `periodSeconds: 30` + `failureThreshold: 3`으로, 최대 150초까지만 기동을 기다립니다. Spring Boot 앱이 150초 안에 기동하지 못하면 Pod가 강제 종료되고 무한 재시작에 빠집니다.

- **livenessProbe의 initialDelaySeconds를 늘리는 방법**: 간단하지만 정확한 해결책이 아닙니다. 기동이 완료된 후에도 불필요하게 긴 초기 대기 시간이 남습니다
- **startupProbe를 별도로 추가하는 방법 (선택)**: startupProbe가 성공할 때까지 livenessProbe와 readinessProbe가 비활성화됩니다. 기동 전용 프로브를 분리함으로써 "기동 대기"와 "상태 확인"의 책임을 명확히 나눌 수 있습니다. order-service에는 같은 이유로 이미 startupProbe를 적용한 전례가 있었습니다

### Inventory Service: 코드 버그 vs 런타임 환경 문제

Smoke Test에서 모든 요청이 500 에러를 반환하므로, 코드 로직의 버그보다는 외부 의존성(Redis, DB) 연결 실패 가능성이 높았습니다.

- **코드 수준 디버깅**: 로컬에서 재현 시도 후 코드를 수정하는 방법입니다. 하지만 로컬에서는 정상 동작할 가능성이 높아 비효율적입니다
- **런타임 로그 분석 우선 (선택)**: `kubectl logs`로 실제 에러 스택을 확인하는 것이 가장 빠른 진단 방법입니다. 500 에러의 원인이 환경 설정이라면 코드 한 줄로 해결될 수 있습니다

### Catalog Service: 보안 설정 디버깅 vs PublicEndpointProvider 확장

Catalog Service의 GET 엔드포인트(`/api/products`, `/api/categories`)는 인증 없이 접근 가능해야 합니다. 기존 `CatalogPublicEndpointProvider`가 `/api/products/**`, `/api/categories/**`를 public으로 선언했는데도 401이 반환되는 상황이었습니다.

문제를 분석해보니, 현재 `PublicEndpointProvider` 인터페이스는 path만 지원하고 HTTP method를 구분하지 않습니다. 이것은 보안 허점이기도 합니다. `/api/products/**`를 public으로 열면 GET뿐 아니라 POST, PUT, DELETE도 인증 없이 호출할 수 있게 됩니다.

- **Catalog Service의 SecurityConfig를 직접 오버라이드**: 서비스별로 SecurityFilterChain을 커스터마이즈하는 방법입니다. 하지만 common-security 모듈의 공통 설정과 충돌할 수 있고, 같은 패턴이 필요한 다른 서비스에서 코드가 중복됩니다
- **PublicEndpointProvider에 HTTP method 지원 추가 (선택)**: 인터페이스에 `publicEndpointsByMethod()` 메서드를 추가하여, 특정 HTTP method에 대해서만 public 접근을 허용합니다. default 구현을 제공하므로 기존 6개 서비스의 Provider가 깨지지 않습니다

## 해결 과정

### 1단계: startupProbe 추가 (Auth 포함 5개 서비스)

order-service에 이미 적용된 startupProbe 패턴을 나머지 5개 서비스에 동일하게 적용합니다.

```yaml
# 변경 전
livenessProbe:
  httpGet:
    path: /actuator/health/liveness
    port: 8080
  initialDelaySeconds: 60
  periodSeconds: 30
  timeoutSeconds: 5
  failureThreshold: 3
```

```yaml
# 변경 후
startupProbe:
  httpGet:
    path: /actuator/health/liveness
    port: 8080
  initialDelaySeconds: 30
  periodSeconds: 10
  timeoutSeconds: 5
  failureThreshold: 20
livenessProbe:
  httpGet:
    path: /actuator/health/liveness
    port: 8080
  periodSeconds: 30
  timeoutSeconds: 5
  failureThreshold: 3
```

startupProbe의 최대 허용 시간은 `30 + (10 x 20) = 230초`입니다. 이 시간 안에 Spring Boot 앱이 기동을 완료하면 startupProbe가 성공으로 전환되고, 그 이후부터 livenessProbe가 활성화됩니다.

livenessProbe에서 `initialDelaySeconds: 60`을 제거한 이유는 startupProbe가 기동 대기를 전담하기 때문입니다. startupProbe가 성공한 시점에서 앱은 이미 기동을 완료한 상태이므로, livenessProbe에 별도의 초기 대기가 필요 없습니다.

### 2단계: Inventory Service Redis 연결 문제 진단 및 수정

`kubectl logs`로 inventory-service의 에러 로그를 확인한 결과, 원인이 바로 드러났습니다.

```
Caused by: io.lettuce.core.RedisConnectionException: Unable to connect to localhost/<unresolved>:6379
Caused by: io.netty.channel.AbstractChannel$AnnotatedConnectException: Connection refused: localhost/127.0.0.1:6379
```

Pod 내부에서 `localhost:6379`로 Redis에 연결을 시도하고 있었습니다. k8s 환경에서 Redis는 외부 호스트(`192.168.75.174`)에 있으므로 당연히 실패합니다.

`application.yaml`에는 `${REDIS_HOST:localhost}`로 환경 변수를 참조하고 있고, ConfigMap에 `REDIS_HOST: 192.168.75.174`가 설정되어 있습니다. Pod 내부에서 `env` 명령으로 확인해도 `REDIS_HOST=192.168.75.174`가 정상적으로 주입되어 있었습니다.

그런데 왜 `localhost`로 연결하는 걸까요? `RedisConfig.kt`를 확인한 결과 원인을 찾았습니다.

```kotlin
// 문제의 코드
@Configuration
class RedisConfig {
    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory = LettuceConnectionFactory()
    // ...
}
```

`LettuceConnectionFactory()`의 **no-arg 생성자**가 문제였습니다. 이 생성자는 `localhost:6379`를 하드코딩합니다. Spring Boot의 auto-configuration은 `spring.data.redis.host` 프로퍼티를 읽어서 `LettuceConnectionFactory`를 자동으로 생성하는데, 이 커스텀 Bean이 auto-configuration을 **오버라이드**하고 있었습니다.

수정은 간단합니다. 수동으로 생성한 `redisConnectionFactory()` Bean을 제거하면 됩니다.

```kotlin
// 수정 후 - redisConnectionFactory Bean 제거
@Configuration
class RedisConfig {
    @Bean
    fun redisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<String, String> =
        RedisTemplate<String, String>().also {
            it.connectionFactory = connectionFactory
            it.keySerializer = StringRedisSerializer()
            it.valueSerializer = StringRedisSerializer()
            it.hashKeySerializer = StringRedisSerializer()
            it.hashValueSerializer = StringRedisSerializer()
            it.afterPropertiesSet()
        }
}
```

`redisTemplate`은 `RedisConnectionFactory`를 파라미터로 주입받고 있으므로, Spring Boot가 auto-configure한 `LettuceConnectionFactory`(프로퍼티 기반)가 자동으로 주입됩니다. `redisTemplate` Bean은 serializer 설정이 필요하므로 그대로 유지합니다.

이 버그가 로컬 환경에서 발견되지 않았던 이유는, 로컬에서는 Redis가 실제로 `localhost:6379`에서 실행되기 때문입니다. `LettuceConnectionFactory()`의 기본값이 우연히 로컬 환경과 일치했던 것입니다.

### 3단계: PublicEndpointProvider HTTP method 지원 추가

#### 인터페이스 확장

기존 `PublicEndpointProvider`에 `publicEndpointsByMethod()` 메서드를 추가합니다. 두 메서드 모두 default 구현을 제공하여 기존 구현체가 깨지지 않도록 합니다.

```kotlin
interface PublicEndpointProvider {
    fun publicEndpoints(): Array<String> = emptyArray()

    fun publicEndpointsByMethod(): Map<HttpMethod, Array<String>> = emptyMap()
}
```

기존에는 `publicEndpoints()`가 abstract 메서드였으므로, default 구현(`emptyArray()`)을 추가하는 것이 핵심입니다. 이렇게 하면 `publicEndpointsByMethod()`만 오버라이드하는 구현체도 컴파일 에러 없이 동작합니다.

#### SecurityConfig 수정

`securityFilterChain`에서 method 기반 permitAll 로직을 추가합니다.

```kotlin
@Bean
fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
    val publicPaths = getPublicEndpoints() + getServiceSpecificPublicEndpoints()
    val methodBasedEndpoints = getMethodBasedPublicEndpoints()

    return http
        .csrf { it.disable() }
        .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
        .exceptionHandling { it.authenticationEntryPoint(jwtAuthenticationEntryPoint) }
        .authorizeHttpRequests { auth ->
            auth.requestMatchers(*publicPaths).permitAll()

            methodBasedEndpoints.forEach { (method, paths) ->
                auth.requestMatchers(method, *paths).permitAll()
            }

            auth.anyRequest().authenticated()
        }
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
        .build()
}
```

여러 `PublicEndpointProvider`에서 같은 HTTP method에 대해 서로 다른 경로를 등록할 수 있으므로, merge 로직을 추가합니다.

```kotlin
private fun getMethodBasedPublicEndpoints(): Map<HttpMethod, Array<String>> {
    val merged = mutableMapOf<HttpMethod, MutableList<String>>()
    publicEndpointProviders.forEach { provider ->
        provider.publicEndpointsByMethod().forEach { (method, paths) ->
            merged.getOrPut(method) { mutableListOf() }.addAll(paths)
        }
    }
    return merged.mapValues { it.value.toTypedArray() }
}
```

#### CatalogPublicEndpointProvider 수정

기존에 path-only로 등록했던 상품/카테고리 경로를 GET method 기반으로 이동합니다.

```kotlin
@Component
class CatalogPublicEndpointProvider : PublicEndpointProvider {
    override fun publicEndpointsByMethod(): Map<HttpMethod, Array<String>> = mapOf(
        HttpMethod.GET to arrayOf(
            "/api/products",
            "/api/products/{id}",
            "/api/products/{id}/skus",
            "/api/categories",
            "/api/categories/tree",
        ),
    )

    override fun publicEndpoints(): Array<String> = arrayOf(
        "/actuator/health/**",
        "/actuator/info",
        "/swagger-ui/**",
        "/v3/api-docs/**",
    )
}
```

변경 전후를 비교하면 다음과 같습니다.

| 엔드포인트 | 변경 전 | 변경 후 |
|-----------|--------|--------|
| `GET /api/products` | public | public |
| `POST /api/products` | **public (보안 허점)** | **authenticated** |
| `GET /api/categories/tree` | public | public |
| `POST /api/categories/tree` | **public (보안 허점)** | **authenticated** |

와일드카드 패턴(`/api/products/**`)을 구체적인 경로(`/api/products`, `/api/products/{id}`)로 변경한 것도 의미가 있습니다. 향후 `/api/products/{id}/reviews`처럼 하위 경로가 추가될 때, 의도치 않게 public으로 노출되는 것을 방지합니다.

### 빌드 검증

```bash
# common-security 빌드
./gradlew :common:common-security:build  # BUILD SUCCESSFUL

# 전체 서비스 컴파일 (인터페이스 호환성 검증)
./gradlew :services:auth-service:compileKotlin \
          :services:user-service:compileKotlin \
          :services:order-service:compileKotlin \
          :services:inventory-service:compileKotlin \
          :services:payment-service:compileKotlin \
          :services:catalog-service:compileKotlin  # BUILD SUCCESSFUL
```

6개 서비스 모두 컴파일에 성공했습니다. `PublicEndpointProvider` 인터페이스에 default 구현을 추가했으므로, 기존 구현체를 수정하지 않아도 호환됩니다.

## 결과

3건의 이슈에 대한 수정 파일은 다음과 같습니다.

| 이슈 | 수정 파일 | 변경 내용 |
|------|----------|----------|
| B-3: startupProbe | `infra/k8s/services/{auth,user,catalog,inventory,payment}-service.yaml` | startupProbe 추가, livenessProbe initialDelaySeconds 제거 |
| B-2: Redis 연결 | `services/inventory-service/.../RedisConfig.kt` | `LettuceConnectionFactory()` Bean 제거 |
| B-1: 401 에러 | `common/common-security/.../PublicEndpointProvider.kt` | `publicEndpointsByMethod()` 메서드 추가 |
| B-1: 401 에러 | `common/common-security/.../SecurityConfig.kt` | method 기반 permitAll 로직 추가 |
| B-1: 401 에러 | `services/catalog-service/.../CatalogPublicEndpointProvider.kt` | GET 엔드포인트를 method 기반으로 이동 |

이번 작업에서 얻은 인사이트는 다음과 같습니다.

- **startupProbe는 Spring Boot 서비스의 필수 설정입니다.** livenessProbe의 `initialDelaySeconds`로 기동 시간을 추정하는 것은 근본적으로 불안정합니다. 기동 시간은 환경(CPU 제한, DB 초기화, Kafka 연결)에 따라 크게 달라질 수 있습니다. startupProbe로 "기동 완료 대기"를 분리하면 이 불확실성을 안전하게 처리할 수 있습니다
- **auto-configuration을 오버라이드하는 커스텀 Bean은 환경 이동 시 잠재적 버그가 됩니다.** `LettuceConnectionFactory()`의 no-arg 생성자가 로컬에서는 우연히 동작했지만, k8s 환경에서는 실패했습니다. Spring Boot의 auto-configuration을 사용하면 프로퍼티만으로 환경별 설정을 분리할 수 있습니다. 커스텀 Bean을 만들기 전에 "auto-configuration이 이미 제공하는 것은 아닌지" 확인하는 습관이 필요합니다
- **보안 설정에서 path-only 허용은 위험합니다.** `requestMatchers("/api/products/**").permitAll()`은 해당 경로의 모든 HTTP method를 열어줍니다. 공개 읽기(GET)와 인증 필수 쓰기(POST/PUT/DELETE)를 구분해야 하는 API에서는 반드시 HTTP method를 함께 지정해야 합니다
