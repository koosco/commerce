# 이벤트 시스템 개선 계획

> 마지막 업데이트: 2025-01-25
> Kafka Consumer 패턴 통일 완료 후 업데이트

## 완료된 작업

### 1. 이벤트 발행(Producer) 패턴 통일

**변경 사항:**

1. **payment-service 네이밍 통일**
   - `IntegrationEventPublisherPort` → `IntegrationEventPublisher`
   - `KafkaIntegrationEventPublisherAdapter` → `KafkaIntegrationEventPublisher`

2. **order-service 패턴 단순화**
   - `pullDomainEvents()` 패턴 제거
   - Integration Event 직접 발행 패턴으로 변경
   - 영향받은 UseCase: `CreateOrderUseCase`, `MarkOrderPaidUseCase`, `CancelOrderByPaymentFailureUseCase`, `CancelOrderByUserUseCase`

3. **CLAUDE.md 문서화**
   - Integration Event Publishing Pattern 섹션 추가
   - Event Publishing by Service 테이블 추가
   - Important Constraints에 네이밍 규칙 추가

**현재 서비스별 이벤트 발행 현황:**

| 서비스 | Kafka 발행 | 패턴 | 비고 |
|--------|-----------|------|------|
| order-service | O | Integration Event 직접 발행 | `@Transactional` 내 발행 |
| inventory-service | O | Integration Event 직접 발행 | 비트랜잭셔널 (Redis 특성) |
| payment-service | O | Integration Event 직접 발행 | 멱등성 저장소 사용 |
| catalog-service | O | Integration Event 직접 발행 | 표준 패턴 |
| user-service | X | - | Feign 동기 호출 (auth-service 연동) |
| auth-service | X | - | 순수 CRUD |

---

### 2. Kafka 통합 테스트 인프라 추가 ✅

**커밋:** `47b53a1` (2025-01-25)

**변경 사항:**

1. **common-core testFixtures 추가**
   - `java-test-fixtures` 플러그인 적용
   - `KafkaContainerTestBase` 공통 테스트 베이스 클래스 제공
   - Testcontainers 의존성 (kafka, junit-jupiter)

2. **각 서비스 통합 테스트 추가**
   - `catalog-service`: KafkaProductSkuEventPublisherIntegrationTest
   - `inventory-service`: KafkaOrderEventConsumerIntegrationTest, KafkaStockEventPublisherIntegrationTest, StockIdempotencyTest
   - `order-service`: KafkaOrderEventPublisherIntegrationTest, KafkaPaymentEventConsumerIntegrationTest, KafkaStockEventConsumerIntegrationTest
   - `payment-service`: KafkaOrderPlacedConsumerIntegrationTest, KafkaPaymentEventPublisherIntegrationTest, PaymentIdempotencyTest

3. **테스트 의존성 추가**
   - testcontainers (kafka, mariadb)
   - awaitility-kotlin (비동기 테스트)
   - mockito-kotlin
   - h2 (단위 테스트용)

---

### 3. Event Schema 문서화 ✅

**커밋:** `47b53a1` (2025-01-25)

**산출물:**
- `common-core/docs/event-contracts.md` - 서비스 간 이벤트 계약 문서

---

### 4. Kafka Consumer 패턴 통일 ✅

**커밋:** `911b11b` (2025-01-25)

**변경 사항:**

1. **MessageContext를 common-core로 이동**
   - `common/common-core/src/main/kotlin/com/koosco/common/core/messaging/MessageContext.kt` 생성
   - 각 서비스의 중복 MessageContext 파일 삭제 (3개)

2. **groupId 표준화**
   - order-service 5개 consumer: `"order-service"` → `"${spring.kafka.consumer.group-id}"`
   - payment-service 1개 consumer: `"${spring.application.name}"` → `"${spring.kafka.consumer.group-id}"`
   - inventory-service: 이미 표준 준수

3. **MessageContext 로깅 추가**
   - `KafkaStockReservedConsumer`, `KafkaStockConfirmedConsumer`에 추적 로깅 추가

**현재 Consumer 패턴:**

| 서비스 | Consumer 수 | groupId | MessageContext | 멱등성 |
|--------|------------|---------|----------------|--------|
| order-service | 5개 | `${property}` ✅ | common-core ✅ | 상태 전이 |
| inventory-service | 4개 | `${property}` ✅ | common-core ✅ | 상태 전이 |
| payment-service | 1개 | `${property}` ✅ | common-core ✅ | DB 멱등성 |

---

## 추천 다음 작업

### 1. Event Schema 버저닝 전략 (우선순위: 높음 ⭐)

**점검 항목:**
- [ ] 이벤트 버저닝 전략 수립 (schema evolution)
- [ ] 하위 호환성 보장 방안
- [ ] 필드 추가/삭제 시 처리 방법

---

### 2. Observability 강화 (우선순위: 중간)

**점검 항목:**
- [ ] correlationId/causationId 활용한 분산 추적 개선
- [ ] 이벤트 발행/소비 메트릭 추가 (Micrometer)
- [ ] Grafana 대시보드에 이벤트 처리량 시각화

---

### 3. DLQ 및 재처리 전략 (우선순위: 낮음)

**점검 항목:**
- [ ] Dead Letter Queue 설정
- [ ] 재처리 전략 수립 (exponential backoff)
- [ ] 실패 이벤트 모니터링/알림

---

### 4. 멱등성 패턴 확산 (우선순위: 낮음)

**점검 항목:**
- [ ] payment-service의 IdempotencyRepository 패턴을 다른 서비스에 적용
- [ ] common-core에 공통 멱등성 유틸리티 추가 검토

---

## 권장 진행 순서

```
✅ 이벤트 발행(Producer) 패턴 통일
   ↓
✅ Kafka 통합 테스트 인프라 추가
   ↓
✅ Event Schema 문서화
   ↓
✅ Kafka Consumer 패턴 통일
   ↓
1. Event Schema 버저닝 전략  ← 다음 추천
   ↓
2. Observability 강화
   ↓
3. DLQ 및 재처리 전략
```

---

## 세션 시작 시 명령어

다음 세션에서 이벤트 버저닝 전략 작업을 시작하려면:

```
이벤트 스키마 버저닝 전략을 수립해줘.
- 하위 호환성 보장 방안
- 필드 추가/삭제 시 처리 방법
- 버전 관리 컨벤션
참고: common-core/docs/event-contracts.md
```

---

## 관련 파일

- `CLAUDE.md` - 프로젝트 가이드 (이벤트 발행/소비 패턴 문서화됨)
- `.claude/skills/mono-kafka.md` - Kafka 가이드 스킬
- `.claude/docs/consumer-unification-plan.md` - Consumer 통일 계획 문서 (완료)
- `common/common-core/` - 공통 이벤트 인프라
- `common/common-core/src/main/kotlin/.../messaging/MessageContext.kt` - 공통 MessageContext
- `common/common-core/docs/event-contracts.md` - 이벤트 계약 문서
- `common/common-core/src/testFixtures/` - 공통 테스트 픽스처 (KafkaContainerTestBase)
