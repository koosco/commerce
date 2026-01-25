# 이벤트 시스템 개선 계획

> 마지막 업데이트: 2025-01-25
> 이전 세션에서 이벤트 발행 패턴 통일 완료 후 작성

## 완료된 작업

### 이벤트 발행(Producer) 패턴 통일

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

## 추천 다음 작업

### 1. Kafka Consumer 패턴 통일 (우선순위: 높음 ⭐)

발행(Producer)을 통일했으니, **소비(Consumer) 패턴**도 점검이 필요합니다.

**점검 항목:**
- [ ] 멱등성 처리 방식 통일 (IdempotencyRepository vs. 다른 방식)
- [ ] Consumer 디렉토리 구조 통일
- [ ] 에러 핸들링 패턴 (DLQ, retry 정책)
- [ ] Consumer 그룹 네이밍 컨벤션

**예상 작업:**
```
services/{service}/
└── infra/
    └── messaging/kafka/consumer/
        └── {Event}Consumer.kt
```

**실행 방법:**
```
subagent를 활용해 각 서비스의 consumer 패턴 분석:
- order-service consumer 패턴
- inventory-service consumer 패턴
- payment-service consumer 패턴
- catalog-service consumer 패턴
```

---

### 2. Event Schema 검증 강화 (우선순위: 중간)

**점검 항목:**
- [ ] `common-core`의 CloudEvent 스펙 준수 여부 확인
- [ ] 서비스 간 이벤트 계약(contract) 문서화
- [ ] 이벤트 버저닝 전략 수립 (schema evolution)

**예상 산출물:**
- `common-core/docs/event-contracts.md` - 이벤트 계약 문서
- 각 서비스별 발행/소비 이벤트 목록

---

### 3. 통합 테스트 추가 (우선순위: 중간)

**점검 항목:**
- [ ] 이벤트 발행 → 소비 E2E 테스트
- [ ] Testcontainers로 Kafka 통합 테스트
- [ ] 멱등성 테스트 (동일 이벤트 중복 처리)

**예상 구조:**
```kotlin
@SpringBootTest
@Testcontainers
class OrderEventIntegrationTest {
    @Container
    val kafka = KafkaContainer(...)

    @Test
    fun `주문 생성 시 OrderPlacedEvent 발행 확인`() { ... }
}
```

---

### 4. Observability 강화 (우선순위: 낮음)

**점검 항목:**
- [ ] correlationId/causationId 활용한 분산 추적
- [ ] 이벤트 발행/소비 메트릭 추가
- [ ] Grafana 대시보드에 이벤트 처리량 시각화

---

## 권장 진행 순서

```
1. Kafka Consumer 패턴 통일  ← 다음 작업 추천
   ↓
2. 통합 테스트 추가
   ↓
3. Event Schema 검증 강화
   ↓
4. Observability 강화
```

---

## 세션 시작 시 명령어

다음 세션에서 Consumer 패턴 통일 작업을 시작하려면:

```
각 서비스의 Kafka Consumer 패턴을 분석하고 통일할 방안을 계획해줘.
subagent를 활용해 빠르게 분석해줘.
참고: .claude/docs/next-steps-event-system.md
```

---

## 관련 파일

- `/Users/koo/CodeSpace/commerce/mono/CLAUDE.md` - 프로젝트 가이드 (이벤트 발행 패턴 문서화됨)
- `/Users/koo/CodeSpace/commerce/mono/.claude/skills/mono-kafka.md` - Kafka 가이드 스킬
- `/Users/koo/CodeSpace/commerce/mono/common/common-core/` - 공통 이벤트 인프라
