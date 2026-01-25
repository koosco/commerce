# Inventory Service

재고 관리 서비스 - Redis와 MariaDB를 활용한 하이브리드 재고 관리 시스템

## 목차

- [개요](#개요)
- [핵심 기술 성과](#핵심-기술-성과)
- [아키텍처](#아키텍처)
- [재고 관리 메커니즘](#재고-관리-메커니즘)
- [동시성 제어](#동시성-제어)
- [이벤트 처리](#이벤트-처리)
- [주요 API](#주요-api)
- [실행 방법](#실행-방법)

---

## 개요

Inventory Service는 상품 재고를 실시간으로 관리하는 서비스입니다. **Redis를 활용한 원자적 재고 연산**과 **MariaDB를 활용한 스냅샷 저장**을 통해 높은 처리량과 데이터 일관성을 동시에 달성합니다.

### 주요 기능

- **재고 초기화**: SKU별 초기 재고 설정
- **재고 예약**: 주문 생성 시 재고 예약 (OrderPlaced 이벤트 소비)
- **재고 확정**: 결제 성공 시 예약 재고 확정 (OrderConfirmed 이벤트 소비)
- **재고 취소**: 주문 취소 시 예약 재고 복구 (OrderCancelled 이벤트 소비)
- **재고 조회**: 실시간 재고 현황 조회 (Redis + MariaDB 스냅샷)
- **재고 증감**: 입고/출고에 따른 재고 수량 조정

### 기술 스택

| Category | Technology |
|----------|------------|
| Language | Kotlin 1.9.25, Java 21 |
| Framework | Spring Boot 3.5.8 |
| Database | MariaDB (JPA) |
| Cache | Redis (Lua Script) |
| Messaging | Apache Kafka |
| Build | Gradle (Kotlin DSL) |

---

## 핵심 기술 성과

### 1. Redis + MariaDB 하이브리드 재고 관리

**문제 상황**

- 재고 조회는 매우 빈번하게 발생하지만, DB 조회는 성능 병목을 유발
- 재고 변경은 트랜잭션 보장이 필요하지만, Redis는 트랜잭션을 지원하지 않음
- 동시 다발적 재고 차감 요청에 대한 **원자성 보장** 필요

**해결 방법**

- **Redis**: 재고의 실시간 Primary Store로 사용 (Lua Script로 원자성 보장)
- **MariaDB**: 주기적 스냅샷 저장 및 감사 로그 역할
- **Lua Script**: 재고 차감, 예약, 확정, 취소 연산을 **단일 원자적 연산**으로 처리

**핵심 구현**

```kotlin
// Redis Lua Script를 통한 재고 예약 (reserve_stock.lua)
local stock = tonumber(redis.call("GET", KEYS[1]))
local reserved = tonumber(redis.call("GET", KEYS[2])) or 0
local qty = tonumber(ARGV[1])

if stock == nil then
  return -1 -- stock key not found
end

if stock < qty then
  return -2 -- not enough stock
end

redis.call("DECRBY", KEYS[1], qty)
redis.call("INCRBY", KEYS[2], qty)

return stock - qty
```

**결과**

- **동시성 제어**: Redis의 단일 스레드 특성과 Lua Script를 활용하여 race condition 방지
- **성능 향상**: 재고 조회 응답 속도 대폭 개선 (DB 부하 감소)
- **데이터 일관성**: 스냅샷을 통해 Redis 장애 시에도 데이터 복구 가능

---

### 2. Lua Script 기반 원자적 재고 연산

**재고 연산의 4단계**

| 연산 | Lua Script | 설명 |
|------|-----------|------|
| **Reserve** | `reserve_stock.lua` | 가용 재고를 확인하고 예약 재고로 전환 |
| **Confirm** | `confirm_stock.lua` | 예약 재고를 확정하고 전체 재고에서 차감 |
| **Cancel** | `cancel_stock.lua` | 예약 재고를 취소하고 가용 재고로 복구 |
| **Decrease** | `decrease_stock.lua` | 전체 재고에서 직접 차감 (출고/폐기) |

**동시성 처리 흐름**

```
주문 생성 (OrderPlaced)
    ↓
재고 예약 (reserve_stock.lua)
    ├── 가용 재고 부족 → StockReservationFailed 발행
    └── 성공 → StockReserved 발행
        ↓
    결제 완료 (OrderConfirmed)
        ↓
    재고 확정 (confirm_stock.lua)
        └── StockConfirmed 발행

주문 취소 (OrderCancelled)
    ↓
재고 취소 (cancel_stock.lua)
    └── 예약 재고 복구
```

**핵심 코드**

```kotlin
// RedisInventoryStockAdapter.kt
override fun reserve(items: List<InventoryStockStorePort.ReserveItem>) {
    items.forEach { item ->
        val result = exec(
            reserveStockScript,
            stockKey(item.skuId),
            reservedKey(item.skuId),
            item.quantity,
        )

        when (result) {
            -1L -> throw NotFoundException(InventoryErrorCode.INVENTORY_NOT_FOUND)
            -2L -> throw NotEnoughStockException(
                skuId = item.skuId,
                requestedQuantity = item.quantity,
            )
        }
    }
}
```

---

### 3. 비트랜잭셔널 Integration Event 발행

**문제 상황**

- Redis는 트랜잭션을 지원하지 않으므로, `@Transactional` 내에서 이벤트를 발행할 수 없음
- DB 트랜잭션과 Kafka 이벤트 발행 간의 **원자성 보장**이 어려움

**해결 방법: Outbox Pattern**

- Redis 연산 성공 후, **Outbox 테이블에 이벤트 저장** (별도 트랜잭션)
- **Debezium CDC**가 Outbox 테이블을 모니터링하고 Kafka로 이벤트 발행
- 이벤트 발행 실패 시에도 재시도 가능 (At-Least-Once Delivery)

**핵심 구현**

```kotlin
// OutboxIntegrationEventPublisher.kt
override fun publish(event: InventoryIntegrationEvent) {
    val cloudEvent = event.toCloudEvent(source)
    val topic = topicResolver.resolve(event)

    val payload = objectMapper.writeValueAsString(cloudEvent)

    val outboxEntry = InventoryOutboxEntry.create(
        aggregateId = event.orderId.toString(),
        eventType = eventType,
        payload = payload,
        topic = topic,
        partitionKey = partitionKey,
    )

    outboxRepository.save(outboxEntry)

    logger.info("Outbox entry saved: type=$eventType, orderId=${event.orderId}")
}
```

**Outbox 테이블 구조**

```kotlin
@Entity
@Table(name = "inventory_outbox")
class InventoryOutboxEntry(
    val aggregateId: String,         // 주문 ID
    val eventType: String,            // StockReserved, StockConfirmed 등
    val payload: String,              // CloudEvent JSON
    val topic: String,                // Kafka 토픽
    val partitionKey: String,         // 파티셔닝 키
    var status: OutboxStatus = PENDING,
    val createdAt: Instant = now(),
)
```

**결과**

- Redis 연산과 이벤트 발행 간의 **최종 일관성(Eventual Consistency)** 보장
- Debezium CDC를 통한 **자동 이벤트 발행** (애플리케이션 재시작 불필요)
- 이벤트 발행 실패 시에도 **재시도 가능** (Outbox 테이블에서 재발행)

---

### 4. 멱등성 보장 (Idempotency)

**문제 상황**

- Kafka는 At-Least-Once 전달을 보장하므로, 동일한 이벤트가 **중복 수신**될 수 있음
- 재고 예약/확정/취소 연산이 중복 실행되면 **데이터 불일치** 발생

**해결 방법: Idempotency Table**

- 처리한 이벤트를 `inventory_event_idempotency` 테이블에 기록
- 이벤트 수신 시, 이미 처리된 이벤트인지 확인 (Fast-path Check)
- 중복 이벤트는 건너뛰고 ACK 처리

**핵심 구현**

```kotlin
// InventoryEventIdempotency.kt
@Entity
@Table(
    uniqueConstraints = [
        UniqueConstraint(
            name = "uq_inventory_idempotency",
            columnNames = ["event_id", "action"],
        ),
    ],
)
class InventoryEventIdempotency(
    val eventId: String,       // CloudEvent ID
    val action: String,        // RESERVE_STOCK, CONFIRM_STOCK, RELEASE_STOCK
    val referenceId: String,   // 주문 ID
)
```

```kotlin
// KafkaOrderPlacedEventConsumer.kt
fun onOrderPlaced(@Valid event: CloudEvent<*>, ack: Acknowledgment) {
    // Fast-path idempotency check
    if (idempotencyChecker.isAlreadyProcessed(event.id, Actions.RESERVE_STOCK)) {
        logger.info("Event already processed: eventId=${event.id}")
        ack.acknowledge()
        return
    }

    reserveStockUseCase.execute(command, context)

    // Record idempotency
    idempotencyChecker.recordProcessed(
        eventId = event.id,
        action = Actions.RESERVE_STOCK,
        referenceId = orderPlaced.orderId.toString(),
    )

    ack.acknowledge()
}
```

**결과**

- 중복 이벤트 처리 방지 (Exactly-Once Semantics 근사)
- 재고 데이터 일관성 유지

---

## 아키텍처

### Clean Architecture 계층 구조

```
inventory-service/
├── api/                          # API Layer
│   ├── controller/               # REST Controllers
│   ├── request/                  # Request DTOs
│   └── response/                 # Response DTOs
│
├── application/                  # Application Layer
│   ├── usecase/                  # Use Cases
│   │   ├── ReserveStockUseCase.kt
│   │   ├── ConfirmStockUseCase.kt
│   │   ├── ReleaseStockUseCase.kt
│   │   └── ...
│   ├── command/                  # Command DTOs
│   ├── result/                   # Result DTOs
│   ├── port/                     # Ports (Interfaces)
│   │   ├── IntegrationEventPublisher.kt
│   │   ├── InventoryStockStorePort.kt
│   │   └── InventoryRepositoryPort.kt
│   └── contract/                 # Event Contracts
│       ├── inbound/              # Consumed Events (OrderPlaced, OrderConfirmed 등)
│       └── outbound/             # Published Events (StockReserved, StockConfirmed 등)
│
├── domain/                       # Domain Layer
│   ├── entity/                   # Entities
│   │   ├── Inventory.kt
│   │   ├── InventorySnapshot.kt
│   │   ├── InventoryOutboxEntry.kt
│   │   └── InventoryEventIdempotency.kt
│   ├── vo/                       # Value Objects
│   │   └── Stock.kt
│   ├── event/                    # Domain Events
│   ├── enums/                    # Enums
│   └── exception/                # Domain Exceptions
│
└── infra/                        # Infrastructure Layer
    ├── storage/                  # Storage Adapters
    │   └── primary/
    │       ├── RedisInventoryStockAdapter.kt
    │       └── RedisInventorySeedAdapter.kt
    ├── messaging/kafka/          # Kafka Adapters
    │   ├── consumer/             # Event Consumers
    │   │   ├── KafkaOrderPlacedEventConsumer.kt
    │   │   ├── KafkaOrderConfirmedEventConsumer.kt
    │   │   └── KafkaOrderCancelledEventConsumer.kt
    │   └── producer/             # Event Publishers
    │       └── OutboxIntegrationEventPublisher.kt
    ├── idempotency/              # Idempotency Support
    │   ├── IdempotencyChecker.kt
    │   └── JpaInventoryIdempotencyRepository.kt
    ├── outbox/                   # Outbox Pattern Support
    │   └── JpaInventoryOutboxRepository.kt
    └── config/                   # Infrastructure Configurations
        ├── RedisConfig.kt
        ├── RedisScriptConfig.kt
        └── KafkaConfig.kt
```

### 계층 간 의존성 규칙

```
api → application → domain ← infra
         ↓
       port (interface)
         ↑
       infra (adapter)
```

- **api**: application에 의존
- **application**: domain과 port(인터페이스)에 의존
- **domain**: 어디에도 의존하지 않음 (순수 비즈니스 로직)
- **infra**: application의 port를 구현 (adapter 패턴)

---

## 재고 관리 메커니즘

### 재고 상태 모델

재고는 **total**(전체 재고)과 **reserved**(예약 재고)로 구성됩니다.

```kotlin
@Embeddable
data class Stock(
    val total: Int,      // 전체 재고
    val reserved: Int,   // 예약 재고
) {
    val available: Int   // 가용 재고 = total - reserved
        get() = total - reserved
}
```

### Redis 키 구조

```
inventory:stock:{skuId}     → 가용 재고 (total - reserved)
inventory:reserved:{skuId}  → 예약 재고
```

**예시**

```
inventory:stock:SKU-001     → 100  (가용 재고)
inventory:reserved:SKU-001  → 20   (예약 재고)
→ 실제 total = 100 + 20 = 120
```

### 재고 연산 흐름

#### 1. 재고 예약 (Reserve)

```
OrderPlaced Event
    ↓
[Lua Script: reserve_stock.lua]
    - 가용 재고 확인 (inventory:stock:{skuId})
    - 예약 재고 증가 (inventory:reserved:{skuId})
    - 가용 재고 감소
    ↓
StockReserved Event 발행 (Outbox)
```

#### 2. 재고 확정 (Confirm)

```
OrderConfirmed Event
    ↓
[Lua Script: confirm_stock.lua]
    - 예약 재고 감소 (inventory:reserved:{skuId})
    ↓
StockConfirmed Event 발행 (Outbox)
```

#### 3. 재고 취소 (Cancel)

```
OrderCancelled Event
    ↓
[Lua Script: cancel_stock.lua]
    - 예약 재고 감소 (inventory:reserved:{skuId})
    - 가용 재고 증가 (inventory:stock:{skuId})
    ↓
예약 복구 완료
```

---

## 동시성 제어

### Redis Lua Script의 원자성

Redis는 **단일 스레드**로 동작하며, Lua Script는 **원자적으로 실행**됩니다.

**문제 상황 (Lua Script 없이 처리할 경우)**

```kotlin
// ❌ Race Condition 발생 가능
val stock = redisTemplate.opsForValue().get(stockKey)?.toInt() ?: 0
if (stock >= quantity) {
    redisTemplate.opsForValue().decrement(stockKey, quantity.toLong())
    // ⚠️ 두 번의 Redis 호출 사이에 다른 요청이 끼어들 수 있음
}
```

**해결: Lua Script를 통한 원자적 연산**

```lua
-- ✅ 단일 원자적 연산으로 처리
local stock = tonumber(redis.call("GET", KEYS[1]))
if stock >= qty then
    redis.call("DECRBY", KEYS[1], qty)
    return stock - qty
else
    return -2  -- not enough stock
end
```

### 동시 요청 처리 테스트

**시나리오**: 100개의 재고에 대해 동시에 200개의 주문 요청

```kotlin
// 예상 결과
- 100개 예약 성공 → StockReserved 발행
- 100개 예약 실패 → StockReservationFailed 발행
```

**실제 결과**: Lua Script를 통해 **정확히 100개만 예약 성공** (Race Condition 없음)

---

## 이벤트 처리

### 소비 이벤트 (Inbound)

| 이벤트 | 토픽 | 처리 내용 | Consumer |
|--------|------|----------|----------|
| `OrderPlaced` | `koosco.commerce.order.placed` | 재고 예약 | `KafkaOrderPlacedEventConsumer` |
| `OrderConfirmed` | `koosco.commerce.order.confirmed` | 재고 확정 | `KafkaOrderConfirmedEventConsumer` |
| `OrderCancelled` | `koosco.commerce.order.cancelled` | 재고 취소 | `KafkaOrderCancelledEventConsumer` |
| `ProductSkuCreated` | `koosco.commerce.sku.created` | 재고 초기화 | `KafkaProductSkuCreatedEventConsumer` |

### 발행 이벤트 (Outbound)

| 이벤트 | 토픽 | 발행 시점 | 소비자 |
|--------|------|----------|--------|
| `StockReserved` | `koosco.commerce.stock.reserved` | 재고 예약 성공 | order-service |
| `StockReservationFailed` | `koosco.commerce.stock.reservation.failed` | 재고 예약 실패 | order-service |
| `StockConfirmed` | `koosco.commerce.stock.confirmed` | 재고 확정 성공 | order-service |
| `StockConfirmFailed` | `koosco.commerce.stock.confirm.failed` | 재고 확정 실패 | order-service |

### Consumer 표준 패턴

```kotlin
@Component
@Validated
class KafkaOrderPlacedEventConsumer(
    private val reserveStockUseCase: ReserveStockUseCase,
    private val idempotencyChecker: IdempotencyChecker,
) {
    @KafkaListener(
        topics = ["\${inventory.topic.mappings.order.placed}"],
        groupId = "\${spring.kafka.consumer.group-id}",
    )
    fun onOrderPlaced(@Valid event: CloudEvent<*>, ack: Acknowledgment) {
        // 1. Null/역직렬화 실패 → ack 후 skip (poison message)
        val payload = event.data ?: run {
            ack.acknowledge()
            return
        }

        // 2. Idempotency 체크 (Fast-path)
        if (idempotencyChecker.isAlreadyProcessed(event.id, Actions.RESERVE_STOCK)) {
            ack.acknowledge()
            return
        }

        // 3. Command 변환 및 UseCase 실행
        val context = MessageContext(
            correlationId = orderPlaced.correlationId,
            causationId = event.id,
        )

        try {
            reserveStockUseCase.execute(command, context)

            // 4. Idempotency 기록
            idempotencyChecker.recordProcessed(event.id, Actions.RESERVE_STOCK, referenceId)

            ack.acknowledge()
        } catch (e: NotEnoughStockException) {
            // 5. 비즈니스 예외 → ack (재시도 불필요)
            ack.acknowledge()
        } catch (e: Exception) {
            // 6. 인프라 예외 → throw (재시도)
            throw e
        }
    }
}
```

---

## 주요 API

### 재고 조회

```bash
GET /api/v1/inventory/{skuId}
```

**Response**

```json
{
  "success": true,
  "data": {
    "skuId": "SKU-001",
    "total": 120,
    "reserved": 20,
    "available": 100,
    "updatedAt": "2025-01-25T10:00:00"
  }
}
```

### 재고 증가 (입고)

```bash
POST /api/v1/admin/inventory/{skuId}/add
Content-Type: application/json

{
  "quantity": 50
}
```

### 재고 감소 (출고)

```bash
POST /api/v1/admin/inventory/{skuId}/decrease
Content-Type: application/json

{
  "quantity": 10
}
```

---

## 실행 방법

### 1. 인프라 실행 (Docker Compose)

```bash
# Redis, MariaDB, Kafka 실행
cd ../../infra/docker
docker-compose up -d
```

### 2. 서비스 빌드 및 실행

```bash
# 빌드
./gradlew :services:inventory-service:build

# 실행
./gradlew :services:inventory-service:bootRun

# 또는
java -jar services/inventory-service/build/libs/inventory-service-0.0.1-SNAPSHOT.jar
```

### 3. Swagger UI 접속

```
http://localhost:8083/swagger-ui.html
```

### 4. Health Check

```bash
curl http://localhost:8083/actuator/health
```

---

## 환경 변수

| 변수 | 기본값 | 설명 |
|------|--------|------|
| `REDIS_HOST` | `localhost` | Redis 호스트 |
| `DB_HOST` | `localhost` | MariaDB 호스트 |
| `DB_PORT` | `3306` | MariaDB 포트 |
| `DB_NAME` | `commerce-inventory` | 데이터베이스 이름 |
| `DB_USERNAME` | `admin` | DB 사용자명 |
| `DB_PASSWORD` | `admin1234` | DB 비밀번호 |
| `SPRING_KAFKA_BOOTSTRAP_SERVERS` | `localhost:9092` | Kafka 브로커 주소 |
| `JWT_SECRET` | (기본값 있음) | JWT 시크릿 키 |

---

## 포트폴리오 핵심 요약

1. **Redis + MariaDB 하이브리드 재고 관리**
   - Redis를 Primary Store로 활용하여 **높은 처리량** 달성
   - MariaDB 스냅샷을 통한 **데이터 복구** 가능

2. **Lua Script 기반 원자적 연산**
   - Redis의 단일 스레드 특성과 Lua Script를 활용한 **동시성 제어**
   - Race Condition 방지 및 **정확한 재고 관리**

3. **Outbox Pattern을 통한 비트랜잭셔널 이벤트 발행**
   - Redis 연산 후 Outbox 테이블에 이벤트 저장
   - Debezium CDC를 통한 **최종 일관성 보장**

4. **멱등성 보장 (Idempotency)**
   - 중복 이벤트 처리 방지
   - Exactly-Once Semantics 근사

5. **Clean Architecture 준수**
   - Port & Adapter 패턴을 통한 계층 분리
   - 비즈니스 로직과 인프라 로직의 명확한 분리

---

## 참고 자료

- [Redis Lua Scripting](https://redis.io/docs/manual/programmability/eval-intro/)
- [Outbox Pattern](https://microservices.io/patterns/data/transactional-outbox.html)
- [Debezium CDC](https://debezium.io/)
- [CloudEvent Specification](https://cloudevents.io/)

---

**Author**: koo
**Last Updated**: 2025-01-25
