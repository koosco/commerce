# Catalog Service - CLAUDE.md

상품 카탈로그 및 카테고리 관리를 담당하는 서비스입니다.

## 개요

| 항목 | 값 |
|------|-----|
| 포트 | 8084 |
| 데이터베이스 | commerce-catalog (MariaDB) |
| Kafka Topic | `koosco.commerce.product.default` |

**핵심 책임**: 상품(Product) 및 SKU 관리, 계층형 카테고리 관리, 옵션 조합 기반 SKU 자동 생성

## Clean Architecture 구조

```
catalog-service/
├── api/                    # Presentation Layer
│   ├── ProductController, CategoryController
│   └── Requests/Responses DTO
├── application/            # Application Layer
│   ├── usecase/           # CreateProductUseCase, FindSkuUseCase 등
│   ├── command/result/    # Command, Result DTO
│   ├── port/              # ProductRepository, IntegrationEventPublisher
│   └── contract/outbound/ # ProductSkuCreatedEvent
├── domain/                 # Domain Layer
│   ├── entity/            # Product, ProductSku, Category, ProductOptionGroup
│   ├── service/           # SkuGenerator, ProductValidator
│   └── vo/enums/          # ProductOptions, ProductStatus
└── infra/                  # Infrastructure Layer
    ├── persist/           # JPA Repository, Adapter
    ├── messaging/kafka/   # OutboxIntegrationEventPublisher
    └── outbox/            # CatalogOutboxRepository
```

## 핵심 기능

1. **계층형 카테고리**: 재귀적 트리 생성, O(n) Map 기반 조회, 동일 부모 하위 중복 방지
2. **SKU 자동 생성**: Cartesian Product 알고리즘, 최대 500개 제한 — 상세: `@services/catalog-service/.claude/docs/sku-generation.md`
3. **Outbox 패턴**: Debezium CDC 연동, 트랜잭션 일관성 보장 — 상세: `@services/catalog-service/.claude/docs/sku-generation.md`

## 이벤트 처리

| 방향 | 이벤트 | Topic | 설명 |
|------|--------|-------|------|
| 발행 | `ProductSkuCreatedEvent` | `koosco.commerce.product.default` | SKU 생성 시 → inventory-service |
| 소비 | 없음 | - | Producer only |

## 도메인 모델

상세: `@services/catalog-service/.claude/docs/domain-model.md`

## API 요약

| Method | Path | 설명 |
|--------|------|------|
| GET | `/api/products`, `/api/products/{id}`, `/api/products/{id}/skus` | 상품 조회 |
| POST | `/api/products` | 상품 생성 (인증 필수) |
| GET | `/api/categories/tree` | 카테고리 트리 |
| POST | `/api/categories/tree` | 트리 생성 (재귀) |

## Public Endpoint 보안

`CatalogPublicEndpointProvider`는 HTTP method 기반으로 공개 엔드포인트를 정의합니다.

- `publicEndpointsByMethod()`: GET만 허용 (상품/카테고리 조회)
- `publicEndpoints()`: actuator, swagger 등 method 무관한 경로

**주의**: 와일드카드(`/api/products/**`) 대신 명시적 경로 + GET method 제한으로 POST/PUT/DELETE는 인증 필요
