# Payment Service

결제 처리 및 Toss Payments API 연동을 담당하는 서비스입니다.

## 목차

- [개요](#개요)
- [핵심 기능](#핵심-기능)
- [아키텍처](#아키텍처)
- [기술적 성과](#기술적-성과)
- [API 엔드포인트](#api-엔드포인트)
- [이벤트 처리](#이벤트-처리)
- [멱등성 보장](#멱등성-보장)
- [Outbox 패턴](#outbox-패턴)
- [도메인 모델](#도메인-모델)
- [에러 처리](#에러-처리)
- [개발 환경 설정](#개발-환경-설정)

## 개요

Payment Service는 주문 시스템에서 결제 생성, 승인, 취소를 처리하며, Toss Payments API와 통합되어 실제 결제 게이트웨이와 연동됩니다. 비동기 이벤트 기반 아키텍처를 통해 order-service와 느슨하게 결합되어 있습니다.

### 포트 정보

- **HTTP 포트**: 8087
- **데이터베이스**: MariaDB (`commerce-payment` 스키마)
- **Kafka Consumer Group**: `payment-service-group`

## 핵심 기능

### 1. 결제 생성 (Payment Creation)

Order-service에서 발행한 `OrderPlacedEvent`를 수신하여 자동으로 결제 건을 생성합니다.

- 주문 ID 기반 결제 엔티티 생성
- 결제 금액 및 사용자 정보 저장
- 결제 상태 초기화 (`READY`)

### 2. 결제 승인 (Payment Approval)

Toss Payments Widget을 통해 사용자가 결제를 승인하면 처리합니다.

- Toss Payments API 연동
- 결제 트랜잭션 기록
- 결제 상태 변경 (`APPROVED`)

### 3. 결제 취소 (Payment Cancellation)

승인된 결제에 대해 취소 처리를 수행합니다.

- 부분 취소 및 전액 취소 지원
- 취소 금액 검증
- 취소 트랜잭션 기록

### 4. Toss Payments 연동

Toss Payments Widget API를 통해 실제 결제 게이트웨이와 통합됩니다.

- Widget Secret Key 관리
- 결제 승인 API 호출
- 결제 실패 처리

## 아키텍처

### Clean Architecture 계층 구조

```
payment-service/
├── api/                    # API Layer (Controller, Request/Response DTOs)
│   ├── WidgetController    # Toss Payments Widget 통합
│   └── PaymentConfirmRequest
│
├── application/            # Application Layer (Use Cases, Commands, Ports)
│   ├── usecase/
│   │   ├── CreatePaymentUseCase       # 결제 생성
│   │   └── ApprovePaymentUseCase      # 결제 승인
│   ├── command/
│   │   ├── CreatePaymentCommand
│   │   └── PaymentApproveCommand
│   ├── port/
│   │   ├── IntegrationEventPublisher  # 이벤트 발행 Port
│   │   ├── PaymentRepositoryPort      # 결제 저장소 Port
│   │   └── IdempotencyRepositoryPort  # 멱등성 저장소 Port
│   └── contract/
│       ├── inbound/
│       │   └── order/
│       │       └── OrderPlacedEvent   # 수신 이벤트
│       └── outbound/
│           └── payment/
│               ├── PaymentCreatedEvent   # 결제 생성 이벤트
│               ├── PaymentCompletedEvent # 결제 승인 완료 이벤트
│               └── PaymentFailedEvent    # 결제 승인 실패 이벤트
│
├── domain/                 # Domain Layer (Entities, Value Objects)
│   ├── entity/
│   │   ├── Payment                    # 결제 Aggregate Root
│   │   ├── PaymentTransaction         # 결제 트랜잭션
│   │   ├── PaymentIdempotency         # 멱등성 엔티티
│   │   └── PaymentOutboxEntry         # Outbox 엔티티
│   ├── vo/
│   │   └── Money                      # 금액 Value Object
│   ├── enums/
│   │   ├── PaymentStatus              # 결제 상태
│   │   └── PaymentAction              # 결제 액션 (CREATE, APPROVE)
│   └── exception/
│       ├── PaymentBusinessException
│       └── DuplicatePaymentException
│
└── infra/                  # Infrastructure Layer (Adapters)
    ├── messaging/kafka/
    │   ├── consumer/
    │   │   └── KafkaOrderPlacedEventConsumer
    │   └── producer/
    │       └── OutboxIntegrationEventPublisher  # Outbox 기반 발행
    ├── persist/
    │   ├── PaymentRepositoryAdapter
    │   ├── IdempotencyRepositoryAdapter
    │   └── jpa/
    │       ├── JpaPaymentRepository
    │       └── JpaIdempotencyRepository
    └── client/
        ├── PaymentGateway               # PaymentGateway Port 인터페이스
        └── TossPaymentGateway           # Toss Payments API Gateway 구현체
```

### 의존성 규칙

- **application/domain → api/infra 의존 금지**
- Domain은 순수 비즈니스 로직만 포함
- Port/Adapter 패턴으로 외부 의존성 분리

## 기술적 성과

### 1. DB 기반 멱등성 보장

#### 문제 상황

분산 시스템에서 동일한 이벤트가 여러 번 전달될 수 있습니다 (네트워크 재시도, Kafka 재처리 등). 결제 생성 이벤트가 중복 수신되면 동일한 주문에 대해 여러 결제가 생성될 수 있습니다.

#### 해결 방법: Idempotency Repository 패턴

`payment_idempotency` 테이블을 사용하여 이벤트 처리 여부를 추적합니다.

**멱등성 엔티티 구조**

```kotlin
@Entity
@Table(
    name = "payment_idempotency",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uq_payment_idempotency",
            columnNames = ["order_id", "action", "idempotency_key"]
        )
    ]
)
class PaymentIdempotency(
    val orderId: Long,
    val action: PaymentAction,           // CREATE, APPROVE
    val idempotencyKey: String,          // CloudEvent ID (causationId)
    val createdAt: LocalDateTime = LocalDateTime.now()
)
```

**멱등성 보장 로직**

```kotlin
@Transactional
fun execute(command: CreatePaymentCommand, context: MessageContext) {
    val idempotencyKey = requireNotNull(context.causationId) {
        "causationId(eventId) must be provided for idempotency"
    }

    try {
        // 1. 멱등성 키 저장 시도 (Unique Constraint)
        idempotencyRepository.save(
            PaymentIdempotency(
                orderId = command.orderId,
                action = PaymentAction.CREATE,
                idempotencyKey = idempotencyKey
            )
        )

        // 2. 중복 결제 체크
        if (paymentRepository.existsByOrderId(command.orderId)) {
            return
        }

        // 3. 결제 생성 및 이벤트 발행
        val savedPayment = paymentRepository.save(Payment(...))
        integrationEventPublisher.publish(PaymentCreatedEvent(...))

    } catch (e: DataIntegrityViolationException) {
        // 이미 처리된 이벤트 → 멱등 성공 처리
        return
    }
}
```

#### 핵심 포인트

1. **Unique Constraint 활용**: `(order_id, action, idempotency_key)` 복합 유니크 제약으로 중복 방지
2. **CloudEvent ID 사용**: 이벤트의 고유 ID를 멱등성 키로 활용
3. **Exception 기반 제어**: `DataIntegrityViolationException` 캐치로 중복 감지
4. **Transaction 내 처리**: 멱등성 키 저장과 비즈니스 로직을 하나의 트랜잭션으로 묶음
5. **Action별 분리**: CREATE/APPROVE 등 액션별로 독립적인 멱등성 보장

### 2. Outbox 패턴을 통한 안정적인 이벤트 발행

#### 문제 상황

결제 생성과 이벤트 발행은 원자적으로 처리되어야 합니다. 결제는 저장되었지만 이벤트 발행이 실패하면 시스템 상태가 불일치하게 됩니다.

#### 해결 방법: Transactional Outbox Pattern + Debezium CDC

**Outbox 엔티티**

```kotlin
@Entity
@Table(name = "payment_outbox")
class PaymentOutboxEntry(
    aggregateId: String,           // paymentId
    eventType: String,              // "payment.created"
    payload: String,                // CloudEvent JSON
    val topic: String,              // Kafka 토픽
    val partitionKey: String        // Kafka 파티션 키
) : OutboxEntry(
    aggregateId = aggregateId,
    aggregateType = "Payment",
    eventType = eventType,
    payload = payload,
    status = OutboxStatus.PENDING
)
```

**Outbox 기반 이벤트 발행**

```kotlin
@Component
class OutboxIntegrationEventPublisher(
    private val outboxRepository: PaymentOutboxRepository,
    private val topicResolver: KafkaTopicResolver,
    private val objectMapper: ObjectMapper
) : IntegrationEventPublisher {

    override fun publish(event: PaymentIntegrationEvent) {
        val cloudEvent = event.toCloudEvent(source)
        val topic = topicResolver.resolve(event)
        val payload = objectMapper.writeValueAsString(cloudEvent)

        val outboxEntry = PaymentOutboxEntry.create(
            aggregateId = event.paymentId,
            eventType = event.getEventType(),
            payload = payload,
            topic = topic,
            partitionKey = event.getPartitionKey()
        )

        // DB에 저장 (트랜잭션 내)
        outboxRepository.save(outboxEntry)
    }
}
```

#### 동작 흐름

1. **비즈니스 로직 실행**: 결제 생성
2. **Outbox 저장**: 동일 트랜잭션 내에서 `payment_outbox` 테이블에 이벤트 저장
3. **CDC 감지**: Debezium이 Outbox 테이블의 INSERT를 감지
4. **Kafka 발행**: Debezium이 자동으로 Kafka에 이벤트 발행

#### 장점

- **원자성 보장**: DB 저장과 이벤트 발행이 원자적으로 처리
- **재시도 가능**: 이벤트 발행 실패 시 CDC가 자동 재시도
- **순서 보장**: `partitionKey` 기반으로 동일 결제의 이벤트 순서 유지
- **장애 복구**: 서비스 재시작 후에도 미발행 이벤트 자동 처리

### 3. Kafka Consumer 멱등성 처리

**Consumer 패턴**

```kotlin
@Component
@Validated
class KafkaOrderPlacedEventConsumer(
    private val createPaymentUseCase: CreatePaymentUseCase
) {
    @KafkaListener(
        topics = ["\${payment.topic.mappings.order.placed}"],
        groupId = "\${spring.kafka.consumer.group-id}"
    )
    fun onOrderPlaced(@Valid event: CloudEvent<*>, ack: Acknowledgment) {
        // 1. Null 체크 및 역직렬화
        val orderPlacedEvent = objectMapper.convertValue(rawData, OrderPlacedEvent::class.java)

        // 2. MessageContext 생성 (멱등성 키 포함)
        val context = MessageContext(
            correlationId = orderPlacedEvent.correlationId,
            causationId = event.id  // 멱등성 키
        )

        try {
            createPaymentUseCase.execute(command, context)
            ack.acknowledge()
        } catch (e: DuplicatePaymentException) {
            ack.acknowledge()  // 비즈니스 중복 → skip
        } catch (e: PaymentBusinessException) {
            ack.acknowledge()  // 비즈니스 실패 → 재시도 불필요
        } catch (e: Exception) {
            throw e  // 인프라 예외 → 재시도
        }
    }
}
```

#### 처리 전략

1. **Poison Message 처리**: 역직렬화 실패 시 즉시 ack
2. **비즈니스 예외**: ack 후 skip (재시도해도 실패)
3. **인프라 예외**: throw하여 Kafka 재시도
4. **멱등성 보장**: `causationId`를 UseCase에 전달하여 중복 방지

### 4. 외부 API 통합 (Toss Payments)

#### 결제 승인 플로우

```
사용자
  → Frontend (Toss Widget)
    → WidgetController.confirmPayment()
      → ApprovePaymentUseCase
        → PaymentGateway.approve() (TossPaymentGateway)
          ├─ 성공: Payment.approve() → PaymentCompletedEvent 발행
          └─ 실패: Payment.fail() → PaymentFailedEvent 발행
```

#### PaymentGateway 인터페이스

```kotlin
interface PaymentGateway {
    fun approve(paymentKey: String, orderId: String, amount: Long): PaymentApprovalResult
}

sealed class PaymentApprovalResult {
    data class Success(val transactionId: String) : PaymentApprovalResult()
    data class Failure(val reason: String, val code: String) : PaymentApprovalResult()
}
```

#### TossPaymentGateway 구현

```kotlin
@Component
class TossPaymentGateway(
    private val tossClient: TossClient,
) : PaymentGateway {

    override fun approve(paymentKey: String, orderId: String, amount: Long): PaymentApprovalResult {
        return try {
            val response = tossClient.confirmPayment(paymentKey, orderId, amount)
            PaymentApprovalResult.Success(transactionId = response.transactionId)
        } catch (e: TossApiException) {
            PaymentApprovalResult.Failure(
                reason = e.message ?: "Unknown error",
                code = e.errorCode,
            )
        }
    }
}
```

#### 주요 고려사항

- **Secret Key 관리**: 테스트 키 사용 (운영 환경은 환경변수)
- **API 에러 처리**: PG사 오류를 `PaymentErrorCode.PAYMENT_GATEWAY_ERROR`로 변환
- **타임아웃 처리**: RestClient 타임아웃 설정
- **재시도 로직**: 일시적 실패에 대한 재시도 전략
- **Port/Adapter 분리**: `PaymentGateway` 인터페이스로 외부 의존성 추상화

## API 엔드포인트

### 결제 승인 (Toss Widget Callback)

사용자가 Toss Widget에서 결제를 완료하면, 프론트엔드가 이 API를 호출하여 결제를 승인합니다.

```http
POST /api/payments/confirm
Content-Type: application/json

{
  "paymentKey": "tgen_...",
  "orderId": 12345,
  "amount": 50000
}
```

**처리 흐름**

1. `orderId`로 결제 엔티티 조회
2. `paymentKey`와 `amount` 검증
3. `PaymentGateway.approve()` 호출 (Toss API)
4. 성공 시: `Payment.approve()` → `PaymentCompletedEvent` 발행
5. 실패 시: `Payment.fail()` → `PaymentFailedEvent` 발행

**응답**

- `200 OK`: 결제 승인 성공 (PaymentCompletedEvent 발행됨)
- `400 Bad Request`: 잘못된 요청 (금액 불일치, 유효하지 않은 paymentKey)
- `404 Not Found`: 결제 정보를 찾을 수 없음
- `500 Internal Server Error`: 서버 오류 (PaymentFailedEvent 발행될 수 있음)

## 이벤트 처리

### 소비 이벤트

| 이벤트 | 토픽 | 설명 | 처리 로직 |
|--------|------|------|----------|
| `OrderPlacedEvent` | `koosco.commerce.order.placed` | 주문 생성됨 | 결제 엔티티 생성 |

**OrderPlacedEvent 구조**

```kotlin
data class OrderPlacedEvent(
    val orderId: Long,
    val userId: Long,
    val payableAmount: Long,          // 결제 금액
    val items: List<OrderItemDto>,
    val correlationId: String,        // 추적 ID
    val causationId: String?          // 멱등성 키
)
```

### 발행 이벤트

| 이벤트 | 토픽 | 설명 | 트리거 |
|--------|------|------|--------|
| `PaymentCreatedEvent` | `koosco.commerce.payment.created` | 결제 생성됨 | 결제 생성 성공 |
| `PaymentCompletedEvent` | `koosco.commerce.payment.completed` | 결제 승인 완료 | 결제 승인 성공 |
| `PaymentFailedEvent` | `koosco.commerce.payment.failed` | 결제 승인 실패 | 결제 승인 실패 |

**PaymentCreatedEvent 구조**

```kotlin
data class PaymentCreatedEvent(
    override val paymentId: String,   // UUID
    val orderId: Long
) : PaymentIntegrationEvent {
    override fun getEventType() = "payment.created"
}
```

**PaymentCompletedEvent 구조**

```kotlin
data class PaymentCompletedEvent(
    override val paymentId: String,
    val orderId: Long,
    val paidAmount: Long,
    val paymentKey: String,
    val correlationId: String,
    val causationId: String,
) : PaymentIntegrationEvent {
    override fun getEventType() = "payment.completed"
}
```

**PaymentFailedEvent 구조**

```kotlin
data class PaymentFailedEvent(
    override val paymentId: String,
    val orderId: Long,
    val failureReason: String,
    val correlationId: String,
    val causationId: String,
) : PaymentIntegrationEvent {
    override fun getEventType() = "payment.failed"
}
```

## 멱등성 보장

### 멱등성 키 전략

- **키 구성**: `(orderId, action, idempotencyKey)`
- **키 소스**: CloudEvent ID (Kafka 메시지의 고유 ID)
- **저장 시점**: UseCase 실행 전 (트랜잭션 시작 직후)

### 멱등성 시나리오

#### 1. 동일 이벤트 재처리

```
Event 1: orderId=123, causationId=abc123
Event 2: orderId=123, causationId=abc123 (재시도)

결과: Event 2는 DataIntegrityViolationException으로 skip
```

#### 2. 서로 다른 주문의 동일 이벤트 ID

```
Event 1: orderId=123, causationId=abc123
Event 2: orderId=456, causationId=abc123

결과: 둘 다 성공 (orderId가 다르므로 unique constraint 통과)
```

#### 3. 동일 주문의 서로 다른 액션

```
Event 1: orderId=123, action=CREATE, causationId=abc123
Event 2: orderId=123, action=APPROVE, causationId=def456

결과: 둘 다 성공 (action이 다르므로 unique constraint 통과)
```

### 멱등성 테스트

통합 테스트에서 다음 시나리오를 검증합니다:

- 신규 이벤트 처리 시 멱등성 키 저장
- 중복 이벤트 처리 시 결제 생성 방지
- 동시 중복 이벤트 처리 (Race Condition)
- 서로 다른 주문의 동일 키 허용
- 동일 주문의 서로 다른 액션 허용
- causationId null 시 처리 실패

테스트 코드: `PaymentIdempotencyTest.kt`

## Outbox 패턴

### Outbox 테이블 구조

```sql
CREATE TABLE payment_outbox (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    aggregate_id VARCHAR(255) NOT NULL,     -- paymentId
    aggregate_type VARCHAR(255) NOT NULL,   -- "Payment"
    event_type VARCHAR(255) NOT NULL,       -- "payment.created"
    payload TEXT NOT NULL,                  -- CloudEvent JSON
    topic VARCHAR(255) NOT NULL,            -- Kafka 토픽
    partition_key VARCHAR(255) NOT NULL,    -- Kafka 파티션 키
    status VARCHAR(50) NOT NULL,            -- PENDING, PUBLISHED
    created_at TIMESTAMP NOT NULL,
    INDEX idx_payment_outbox_status (status, created_at)
);
```

### Debezium CDC 설정

Debezium Outbox Event Router가 `payment_outbox` 테이블을 모니터링하여 자동으로 Kafka에 발행합니다.

**주요 설정**

- **Source Table**: `payment_outbox`
- **Routing Key**: `partition_key` 컬럼 값
- **Topic**: `topic` 컬럼에 지정된 토픽
- **Payload**: `payload` 컬럼의 CloudEvent JSON

### Outbox 장점

1. **At-least-once 보장**: 이벤트가 최소 한 번은 발행됨
2. **트랜잭션 일관성**: DB 변경과 이벤트 발행의 원자성
3. **자동 재시도**: CDC가 실패 시 자동으로 재시도
4. **순서 보장**: `partition_key` 기반 파티셔닝

## 도메인 모델

### Payment (Aggregate Root)

결제의 전체 생명주기를 관리하는 Aggregate Root입니다.

```kotlin
@Entity
class Payment(
    val paymentId: UUID,        // 외부 노출 ID
    val orderId: Long,          // 주문 ID
    val userId: Long,           // 사용자 ID
    val amount: Money           // 결제 금액 (Value Object)
) {
    var status: PaymentStatus = PaymentStatus.READY

    fun approve(transaction: PaymentTransaction) {
        require(status == PaymentStatus.READY)
        require(transaction.amount == amount)
        transactions.add(transaction)
        status = PaymentStatus.APPROVED
    }

    fun fail(transaction: PaymentTransaction) {
        require(status == PaymentStatus.READY)
        transactions.add(transaction)
        status = PaymentStatus.FAILED
    }

    fun cancel(transaction: PaymentTransaction) {
        require(status == PaymentStatus.APPROVED)
        transactions.add(transaction)
        status = PaymentStatus.CANCELED
    }
}
```

### PaymentStatus

```kotlin
enum class PaymentStatus {
    READY,       // 결제 준비 완료
    APPROVED,    // 승인 완료
    FAILED,      // 승인 실패
    CANCELED     // 결제 취소
}
```

### PaymentTransaction

결제의 각 거래 이력을 기록합니다.

```kotlin
@Entity
class PaymentTransaction(
    val payment: Payment,
    val type: PaymentTransactionType,      // APPROVAL, CANCEL
    val status: PaymentTransactionStatus,  // SUCCESS, FAILED
    val pgTransactionId: String?,          // PG사 거래 ID
    val amount: Money,
    val occurredAt: LocalDateTime
)
```

### Money (Value Object)

금액을 표현하는 불변 Value Object입니다.

```kotlin
@Embeddable
data class Money(
    @Column(name = "amount")
    val value: Long
) {
    init {
        require(value >= 0) { "금액은 0 이상이어야 합니다" }
    }

    operator fun plus(other: Money) = Money(value + other.value)
    operator fun minus(other: Money) = Money(value - other.value)
}
```

## 에러 처리

### 도메인 에러 코드

```kotlin
enum class PaymentErrorCode(
    override val code: String,
    override val message: String,
    override val status: HttpStatus
) : ErrorCode {

    // 400 Bad Request
    INVALID_PAYMENT_AMOUNT("PAYMENT-400-001", "결제 금액이 올바르지 않습니다.", BAD_REQUEST),
    INVALID_PAYMENT_STATUS("PAYMENT-400-002", "결제 상태가 올바르지 않습니다.", BAD_REQUEST),
    PAYMENT_NOT_READY("PAYMENT-400-003", "결제가 가능한 상태가 아닙니다.", BAD_REQUEST),

    // 404 Not Found
    PAYMENT_NOT_FOUND("PAYMENT-404-001", "결제 정보를 찾을 수 없습니다.", NOT_FOUND),

    // 409 Conflict
    DUPLICATE_PAYMENT_REQUEST("PAYMENT-409-001", "중복된 결제 요청입니다.", CONFLICT),
    PAYMENT_ALREADY_APPROVED("PAYMENT-409-002", "이미 승인된 결제입니다.", CONFLICT),

    // 502 Bad Gateway (PG 오류)
    PAYMENT_GATEWAY_ERROR("PAYMENT-502-001", "결제 게이트웨이 오류가 발생했습니다.", BAD_GATEWAY)
}
```

### 에러 응답 예시

```json
{
  "success": false,
  "error": {
    "code": "PAYMENT-404-001",
    "message": "결제 정보를 찾을 수 없습니다.",
    "timestamp": "2025-01-25T10:30:00"
  }
}
```

## 개발 환경 설정

### 필수 환경변수

```bash
# Database
DB_HOST=localhost
DB_PORT=3306
DB_NAME=commerce-payment
DB_USERNAME=admin
DB_PASSWORD=admin1234

# Kafka
SPRING_KAFKA_BOOTSTRAP_SERVERS=localhost:9092
SPRING_KAFKA_CONSUMER_GROUP_ID=payment-service-group

# JWT (common-security 사용)
JWT_SECRET=mySecretKeyForJWTWhichShouldBeAtLeast256BitsLongToEnsureSecurityAndCompliance
```

### 로컬 실행

#### 1. 인프라 실행 (DB, Redis)

```bash
cd /Users/koo/CodeSpace/commerce/mono/infra/docker
docker-compose up -d
```

#### 2. Kafka 실행

```bash
cd /Users/koo/CodeSpace/commerce/mono/infra/kafka
make kafka-local
```

#### 3. Payment Service 빌드 및 실행

```bash
# 프로젝트 루트에서
./gradlew :services:payment-service:build

# 실행
./gradlew :services:payment-service:bootRun
```

#### 4. API 문서 확인

```
http://localhost:8087/swagger-ui.html
```

### 테스트 실행

```bash
# 단위 + 통합 테스트
./gradlew :services:payment-service:test

# 특정 테스트
./gradlew :services:payment-service:test --tests PaymentIdempotencyTest
```

## 주요 의존성

```kotlin
dependencies {
    // Common Modules
    implementation(project(":common:common-core"))
    implementation(project(":common:common-security"))
    implementation(project(":common:common-observability"))

    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // Kafka
    implementation("org.springframework.kafka:spring-kafka")

    // Database
    runtimeOnly("org.mariadb.jdbc:mariadb-java-client")

    // Monitoring
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-registry-prometheus")
}
```

## 모니터링

### Actuator Endpoints

```bash
# Health Check
curl http://localhost:8087/actuator/health

# Prometheus Metrics
curl http://localhost:8087/actuator/prometheus
```

### 주요 메트릭

- `kafka_consumer_records_consumed_total`: 소비된 Kafka 메시지 수
- `payment_created_total`: 생성된 결제 수
- `payment_approved_total`: 승인된 결제 수
- `idempotency_duplicate_prevented_total`: 멱등성으로 방지된 중복 처리 수

### Grafana 대시보드

```
http://localhost:3000 (admin/admin123)
```

Payment Service 전용 대시보드에서 다음을 모니터링합니다:

- 결제 생성/승인 비율
- 이벤트 처리 지연 시간
- Outbox 발행 상태
- 멱등성 중복 방지 현황

## 포트폴리오 포인트

### 1. DB 기반 멱등성 보장 패턴

- **문제**: 분산 시스템의 중복 이벤트 처리
- **해결**: Unique Constraint 기반 멱등성 저장소 구현
- **성과**: 중복 결제 생성 완전 방지, 통합 테스트로 검증

### 2. Transactional Outbox Pattern

- **문제**: 비즈니스 로직과 이벤트 발행의 원자성
- **해결**: Outbox + Debezium CDC 조합
- **성과**: At-least-once 보장, 장애 복구 자동화

### 3. 외부 API 통합 (Toss Payments)

- **문제**: 외부 PG사와의 안정적인 통합
- **해결**: Port/Adapter 패턴 (`PaymentGateway` 인터페이스 + `TossPaymentGateway` 구현체)
- **성과**: Clean Architecture 유지, 테스트 가능성 확보, Mock 구현으로 단위 테스트 용이

### 4. 이벤트 기반 비동기 처리

- **문제**: Order-service와의 느슨한 결합
- **해결**: Kafka Consumer + Integration Event 패턴
- **성과**: 서비스 독립성 확보, 장애 격리

### 5. Choreography Saga 보상 트랜잭션

- **문제**: 결제 실패 시 분산 시스템의 상태 일관성 유지
- **해결**: `PaymentFailedEvent` 발행 → Order-service가 주문 취소 → Inventory-service가 재고 해제
- **성과**: 중앙 조정자 없이 이벤트 체인으로 보상 트랜잭션 구현

## 참고 문서

- [Toss Payments API 문서](https://docs.tosspayments.com/)
- [Transactional Outbox Pattern](https://microservices.io/patterns/data/transactional-outbox.html)
- [Debezium Outbox Event Router](https://debezium.io/documentation/reference/transformations/outbox-event-router.html)
- [CloudEvent Specification](https://cloudevents.io/)
