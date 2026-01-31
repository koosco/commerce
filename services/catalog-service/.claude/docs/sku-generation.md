# SKU 자동 생성 + Outbox 패턴

## SKU 자동 생성 (Cartesian Product)

```
옵션 그룹: 색상[빨강, 파랑], 사이즈[S, M, L]
→ 생성되는 SKU: 6개 (2 x 3)
```

- `SkuGenerator`: 재귀 함수로 Cartesian Product 계산
- `ProductValidator`: SKU 개수 제한 검증 (최대 500개)
- SKU ID 형식: `{productCode}-{optionString}-{hash}`

## Outbox 패턴 기반 이벤트 발행

```
UseCase (@Transactional)
  → Product 저장
  → IntegrationEventPublisher.publish()
  → CatalogOutboxEntry 저장 (같은 트랜잭션)
  → Debezium CDC가 Kafka로 발행
```

**트랜잭션 보장**: DB 저장과 이벤트 발행이 원자적으로 처리됨
