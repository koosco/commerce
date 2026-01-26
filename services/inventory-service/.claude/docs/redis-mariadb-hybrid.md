# Redis + MariaDB 하이브리드 재고 관리

## 핵심 기술 성과 #1: Redis + MariaDB 하이브리드

### 문제 상황

- 재고 조회는 매우 빈번하게 발생하지만, DB 조회는 성능 병목을 유발
- 재고 변경은 트랜잭션 보장이 필요하지만, Redis는 트랜잭션을 지원하지 않음
- 동시 다발적 재고 차감 요청에 대한 **원자성 보장** 필요

### 해결 방법

- **Redis**: 재고의 실시간 Primary Store로 사용 (Lua Script로 원자성 보장)
- **MariaDB**: 주기적 스냅샷 저장 및 감사 로그 역할
- **Lua Script**: 재고 차감, 예약, 확정, 취소 연산을 **단일 원자적 연산**으로 처리

### 결과

- **동시성 제어**: Redis의 단일 스레드 특성과 Lua Script를 활용하여 race condition 방지
- **성능 향상**: 재고 조회 응답 속도 대폭 개선 (DB 부하 감소)
- **데이터 일관성**: 스냅샷을 통해 Redis 장애 시에도 데이터 복구 가능

---

## Lua Script 기반 원자적 연산

### 재고 연산의 4단계

| 연산 | Lua Script | 설명 |
|------|-----------|------|
| **Reserve** | `reserve_stock.lua` | 가용 재고를 확인하고 예약 재고로 전환 |
| **Confirm** | `confirm_stock.lua` | 예약 재고를 확정하고 전체 재고에서 차감 |
| **Cancel** | `cancel_stock.lua` | 예약 재고를 취소하고 가용 재고로 복구 |
| **Decrease** | `decrease_stock.lua` | 전체 재고에서 직접 차감 (출고/폐기) |

### 핵심 구현: reserve_stock.lua

```lua
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

### Kotlin Adapter 구현

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
inventory:stock:{skuId}     -> 가용 재고 (total - reserved)
inventory:reserved:{skuId}  -> 예약 재고
```

**예시**

```
inventory:stock:SKU-001     -> 100  (가용 재고)
inventory:reserved:SKU-001  -> 20   (예약 재고)
-> 실제 total = 100 + 20 = 120
```

### 재고 연산 흐름

#### 1. 재고 예약 (Reserve)

```
OrderPlaced Event
    |
[Lua Script: reserve_stock.lua]
    - 가용 재고 확인 (inventory:stock:{skuId})
    - 예약 재고 증가 (inventory:reserved:{skuId})
    - 가용 재고 감소
    |
StockReserved Event 발행 (Outbox)
```

#### 2. 재고 확정 (Confirm)

```
OrderConfirmed Event
    |
[Lua Script: confirm_stock.lua]
    - 예약 재고 감소 (inventory:reserved:{skuId})
    |
StockConfirmed Event 발행 (Outbox)
```

#### 3. 재고 취소 (Cancel)

```
OrderCancelled Event
    |
[Lua Script: cancel_stock.lua]
    - 예약 재고 감소 (inventory:reserved:{skuId})
    - 가용 재고 증가 (inventory:stock:{skuId})
    |
예약 복구 완료
```

---

## 동시성 제어

### Redis Lua Script의 원자성

Redis는 **단일 스레드**로 동작하며, Lua Script는 **원자적으로 실행**됩니다.

### 문제 상황 (Lua Script 없이 처리할 경우)

```kotlin
// Race Condition 발생 가능
val stock = redisTemplate.opsForValue().get(stockKey)?.toInt() ?: 0
if (stock >= quantity) {
    redisTemplate.opsForValue().decrement(stockKey, quantity.toLong())
    // 두 번의 Redis 호출 사이에 다른 요청이 끼어들 수 있음
}
```

### 해결: Lua Script를 통한 원자적 연산

```lua
-- 단일 원자적 연산으로 처리
local stock = tonumber(redis.call("GET", KEYS[1]))
if stock >= qty then
    redis.call("DECRBY", KEYS[1], qty)
    return stock - qty
else
    return -2  -- not enough stock
end
```

### 동시 요청 처리 검증

**시나리오**: 100개의 재고에 대해 동시에 200개의 주문 요청

```
예상 결과:
- 100개 예약 성공 -> StockReserved 발행
- 100개 예약 실패 -> StockReservationFailed 발행
```

**실제 결과**: Lua Script를 통해 **정확히 100개만 예약 성공** (Race Condition 없음)

---

## 비트랜잭셔널 Integration Event 발행

### 문제 상황

- Redis는 트랜잭션을 지원하지 않으므로, `@Transactional` 내에서 이벤트를 발행할 수 없음
- DB 트랜잭션과 Kafka 이벤트 발행 간의 **원자성 보장**이 어려움

### 해결 방법: Outbox Pattern

- Redis 연산 성공 후, **Outbox 테이블에 이벤트 저장** (별도 트랜잭션)
- **Debezium CDC**가 Outbox 테이블을 모니터링하고 Kafka로 이벤트 발행
- 이벤트 발행 실패 시에도 재시도 가능 (At-Least-Once Delivery)

### Outbox 테이블 구조

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

### 핵심 구현

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

### 결과

- Redis 연산과 이벤트 발행 간의 **최종 일관성(Eventual Consistency)** 보장
- Debezium CDC를 통한 **자동 이벤트 발행** (애플리케이션 재시작 불필요)
- 이벤트 발행 실패 시에도 **재시도 가능** (Outbox 테이블에서 재발행)

---

## 멱등성 보장 (Idempotency)

### 문제 상황

- Kafka는 At-Least-Once 전달을 보장하므로, 동일한 이벤트가 **중복 수신**될 수 있음
- 재고 예약/확정/취소 연산이 중복 실행되면 **데이터 불일치** 발생

### 해결 방법: Idempotency Table

- 처리한 이벤트를 `inventory_event_idempotency` 테이블에 기록
- 이벤트 수신 시, 이미 처리된 이벤트인지 확인 (Fast-path Check)
- 중복 이벤트는 건너뛰고 ACK 처리

### 핵심 구현

```kotlin
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
// Consumer에서 사용
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

### 결과

- 중복 이벤트 처리 방지 (Exactly-Once Semantics 근사)
- 재고 데이터 일관성 유지
