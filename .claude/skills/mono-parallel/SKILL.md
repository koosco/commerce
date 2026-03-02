---
name: mono-parallel
description: 여러 서비스에 대한 작업을 subagent를 통해 병렬로 실행합니다. 여러 서비스에 동일한 패턴의 기능 추가, 동시 수정, 멀티 서비스 리팩토링이 필요할 때 사용합니다.
---

## 동작 방식

1. 사용자의 요청에서 대상 서비스 목록을 파싱합니다
2. 각 서비스별로 독립적인 subagent를 생성합니다
3. 모든 subagent는 병렬로 실행됩니다
4. 각 subagent에게 `/sc:implement` 명령과 함께 서비스별 작업을 전달합니다

## 실행 절차

### 1. 대상 서비스 식별

| 서비스               | 모듈 경로                        | 포트   |
|-------------------|------------------------------|------|
| auth-service      | `services/auth-service`      | 8089 |
| user-service      | `services/user-service`      | 8081 |
| catalog-service   | `services/catalog-service`   | 8084 |
| inventory-service | `services/inventory-service` | 8083 |
| order-service     | `services/order-service`     | 8085 |
| payment-service   | `services/payment-service`   | 8087 |

"모든 서비스" 또는 "전체 서비스"라고 하면 6개 서비스 모두를 대상으로 합니다.

### 2. Subagent 병렬 실행

**CRITICAL**: 반드시 하나의 메시지에서 여러 Task 도구를 동시에 호출해야 합니다.

### 3. Subagent 프롬프트 템플릿

```
/sc:implement 를 사용하여 다음 작업을 수행하세요:

서비스: {service-name}
경로: services/{service-name}

작업 내용:
{user-request-detail}

주의사항:
- 이 서비스만 수정하세요
- Clean Architecture 규칙을 따르세요
- 작업 완료 후 spotlessApply를 실행하세요
- 테스트가 있다면 테스트도 실행하세요
```

Kafka 이벤트 작업 시 추가:

```
- 반드시 CloudEvent 포맷을 사용하세요
- CloudEvent 스펙: common/common-core/src/main/kotlin/com/koosco/common/core/event/CloudEvent.kt
- /common-core-event 스킬을 참조하세요
```

### 4. 결과 수집

모든 subagent 완료 후 각 서비스별 작업 결과 요약, 실패 상세, 추가 조치 보고.

## 사용 예시

```
사용자: 모든 서비스에 /health 엔드포인트를 추가해줘

실행: 6개 Task 도구를 동시에 호출 (각 서비스별 /sc:implement)
```

## Subagent 설정

| 설정                | 값                      |
|-------------------|------------------------|
| subagent_type     | `general-purpose`      |
| model             | 기본 (상속)                |
| run_in_background | `true` (권장, 오래 걸리는 작업) |

## 서비스 그룹

| 그룹            | 서비스                                            |
|---------------|------------------------------------------------|
| all / 전체      | auth, user, catalog, inventory, order, payment |
| kafka-enabled | catalog, inventory, order, payment             |
| auth-related  | auth, user                                     |
| commerce-core | catalog, inventory, order, payment             |
| order-flow    | order, payment, inventory                      |

## 주의사항

1. **독립성**: 각 subagent는 서로의 작업에 영향을 주지 않아야 합니다
2. **충돌 방지**: common 모듈 수정이 필요하면 먼저 처리 후 서비스 작업 실행
3. **빌드 검증**: 모든 작업 완료 후 `./gradlew spotlessCheck build -x test`
4. **병렬 한계**: 너무 많은 subagent(6개 초과)는 리소스 문제 발생 가능
5. **Kafka 작업**: 이벤트 발행/소비 작업 시 CloudEvent 포맷 필수
