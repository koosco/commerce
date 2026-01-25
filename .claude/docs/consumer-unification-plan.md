# Kafka Consumer 패턴 통일 계획

> 작성일: 2025-01-25
> 선행 작업: 이벤트 발행(Producer) 패턴 통일 완료

## 현황 분석 결과

### 서비스별 Consumer 현황

| 서비스 | Consumer 수 | 멱등성 | Group ID | 에러 핸들링 |
|--------|------------|--------|----------|------------|
| order-service | 5개 | 없음 | **불일치** (혼용) | 부분 구현 |
| inventory-service | 4개 | 없음 (Redis 의존) | 일관됨 | 가장 견고 |
| payment-service | 1개 | **DB 멱등성** | 일관됨 | 구현됨 |
| catalog-service | 0개 | - | - | - |

### 주요 불일치 사항

1. **Group ID 네이밍**
   - order-service: `${spring.application.name}` vs `order-service-group` 혼용
   - 다른 서비스: 일관된 패턴 사용

2. **멱등성 처리**
   - payment-service만 DB 레벨 멱등성 (`PaymentIdempotency` 테이블)
   - 나머지는 상태 전이로 암시적 처리

3. **에러 핸들링**
   - DLQ 설정 없음 (TODO 주석만 존재)
   - 서비스별 다른 예외 처리 패턴

4. **Validation 어노테이션**
   - inventory/payment: `@Validated` + `@Valid` 일관 사용
   - order-service: 부분적 사용

---

## 변경 범위

### 필수 변경 (Phase 1)

| 서비스 | 변경 내용 | 파일 수 | 복잡도 |
|--------|----------|--------|--------|
| order-service | Group ID 통일, Validation 추가 | 5개 | 낮음 |
| inventory-service | 변경 없음 (표준 모델) | 0개 | - |
| payment-service | 변경 없음 (이미 준수) | 0개 | - |

### 선택 변경 (Phase 2)

| 항목 | 설명 | 영향 서비스 |
|------|------|------------|
| 멱등성 표준화 | IdempotencyRepository 패턴 확산 | order, inventory |
| DLQ 설정 | 공통 에러 핸들러 + DLQ 토픽 | 전체 |
| 에러 핸들링 통일 | common-core에 공통 패턴 추가 | 전체 |

---

## 작업 분리 전략

### Option A: Git Worktree (병렬 브랜치 작업)

```
mono/                          # main worktree
├── .worktrees/
│   ├── consumer-order/        # order-service 작업용
│   ├── consumer-inventory/    # inventory-service 작업용 (검증만)
│   └── consumer-payment/      # payment-service 작업용 (검증만)
```

**장점:**
- 각 서비스 독립적으로 빌드/테스트 가능
- 충돌 최소화
- 개별 PR 생성 가능

**단점:**
- 이 프로젝트에서는 **변경 범위가 작아 오버헤드**
- order-service만 실제 변경 필요

**결론: 권장하지 않음** - 변경 범위가 order-service 5개 파일로 한정됨

---

### Option B: Subagents 활용 (권장 ✓)

**Phase 1 실행 계획:**

```
┌─────────────────────────────────────────────────────────────────┐
│                      Main Agent (Orchestrator)                   │
├─────────────────────────────────────────────────────────────────┤
│  1. 작업 지시 및 결과 통합                                        │
│  2. 빌드/테스트 검증                                              │
└─────────────────────────────────────────────────────────────────┘
                              │
        ┌─────────────────────┼─────────────────────┐
        ▼                     ▼                     ▼
┌───────────────┐    ┌───────────────┐    ┌───────────────┐
│  Subagent 1   │    │  Subagent 2   │    │  Subagent 3   │
│  order-service│    │ inventory-svc │    │ payment-svc   │
├───────────────┤    ├───────────────┤    ├───────────────┤
│ - Group ID    │    │ - 검증만      │    │ - 검증만      │
│ - Validation  │    │ - 표준 문서화 │    │ - 표준 문서화 │
│ - 5개 파일    │    │ - 0개 변경    │    │ - 0개 변경    │
└───────────────┘    └───────────────┘    └───────────────┘
        │                     │                     │
        └─────────────────────┼─────────────────────┘
                              ▼
                    ┌─────────────────┐
                    │   Build & Test  │
                    │   ./gradlew     │
                    └─────────────────┘
```

**실행 시간 예상:**
- 순차 실행: ~15분
- 병렬 Subagent: ~5분

---

## 구체적 실행 계획

### Phase 1: order-service Group ID & Validation 통일

**변경 파일:**
```
services/order-service/src/main/kotlin/com/koosco/orderservice/order/infra/messaging/kafka/consumer/
├── KafkaPaymentCreatedConsumer.kt      # groupId 수정, @Valid 추가
├── KafkaPaymentCompletedConsumer.kt    # groupId 수정, @Valid 추가
├── KafkaPaymentFailedConsumer.kt       # groupId 수정, @Valid 추가
├── KafkaStockReservedConsumer.kt       # groupId 수정, @Valid 추가
└── KafkaStockConfirmedConsumer.kt      # groupId 수정, @Valid 추가
```

**변경 내용:**
```kotlin
// Before
@KafkaListener(
    topics = ["..."],
    groupId = "\${spring.application.name}"  // 불일치
)
fun consume(event: CloudEvent<...>) { ... }

// After
@KafkaListener(
    topics = ["..."],
    groupId = "order-service"  // 통일
)
fun consume(@Valid event: CloudEvent<...>) { ... }  // @Valid 추가
```

**Subagent 명령:**
```
order-service의 Kafka Consumer 파일 5개를 수정해줘:
1. groupId를 "order-service"로 통일
2. consume 메서드 파라미터에 @Valid 어노테이션 추가
3. 클래스에 @Validated 어노테이션 확인/추가

파일 위치: services/order-service/.../infra/messaging/kafka/consumer/
```

---

### Phase 2: 검증 및 문서화 (병렬 실행)

**Subagent 2 (inventory-service):**
```
inventory-service Consumer를 검증하고 표준 패턴으로 문서화해줘:
- KafkaConsumerConfig.kt 설정 확인
- 4개 Consumer의 에러 핸들링 패턴 정리
- CLAUDE.md에 Consumer 패턴 섹션 추가 제안
```

**Subagent 3 (payment-service):**
```
payment-service의 멱등성 패턴을 분석하고 문서화해줘:
- IdempotencyRepository 패턴 정리
- 다른 서비스 적용 가능성 평가
- common-core 추가 여부 제안
```

---

## 실행 명령어

### 즉시 실행 (Phase 1만)

```
order-service의 Kafka Consumer Group ID와 Validation을 통일해줘.
변경 대상: services/order-service/.../consumer/ 내 5개 파일
- groupId: "order-service"로 통일
- @Valid 어노테이션 추가
- 빌드 및 테스트 검증
```

### 전체 실행 (Phase 1 + 2 병렬)

```
Kafka Consumer 패턴 통일 작업을 subagent로 병렬 실행해줘:

Subagent 1: order-service Consumer 수정 (groupId 통일, @Valid 추가)
Subagent 2: inventory-service 검증 및 표준 패턴 문서화
Subagent 3: payment-service 멱등성 패턴 문서화

참고: .claude/docs/consumer-unification-plan.md
```

---

## 예상 결과

### 변경 후 상태

| 서비스 | Group ID | Validation | 멱등성 | 에러 핸들링 |
|--------|----------|------------|--------|------------|
| order-service | `order-service` ✓ | `@Valid` ✓ | 상태전이 | 현행 유지 |
| inventory-service | `inventory-service` ✓ | `@Valid` ✓ | Redis 특성 | 표준 모델 |
| payment-service | `payment-service` ✓ | `@Valid` ✓ | DB 멱등성 | 표준 모델 |

### 산출물

1. order-service Consumer 5개 파일 수정
2. CLAUDE.md Consumer 패턴 섹션 추가
3. (선택) common-core 멱등성 유틸리티 제안서

---

## 후속 작업 (Phase 3 - 선택)

| 작업 | 우선순위 | 설명 |
|------|---------|------|
| DLQ 설정 | 중간 | 공통 DLQ 토픽 + 에러 핸들러 |
| 멱등성 확산 | 낮음 | payment 패턴을 order에 적용 |
| 공통 에러 핸들러 | 낮음 | common-core에 추가 |

---

## 관련 파일

- 분석 기준: `.claude/docs/next-steps-event-system.md`
- order-service consumers: `services/order-service/.../consumer/`
- inventory-service consumers: `services/inventory-service/.../consumer/`
- payment-service consumer: `services/payment-service/.../consumer/`
