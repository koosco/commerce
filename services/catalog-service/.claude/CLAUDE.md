# Catalog Service - CLAUDE.md

상품 카탈로그 및 카테고리 관리를 담당하는 서비스입니다.

## 개요

| 항목 | 값 |
|------|-----|
| 포트 | 8084 |
| 데이터베이스 | commerce-catalog (MariaDB) |
| Kafka Topic | `koosco.commerce.product.default` |

**핵심 책임**
- 상품(Product) 및 SKU 관리
- 계층형 카테고리 관리
- 옵션 조합 기반 SKU 자동 생성

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

**의존성 규칙**: api -> application -> domain <- infra (domain은 infra에 의존하지 않음)

## 핵심 기능

### 1. 계층형 카테고리 관리

- **재귀적 트리 생성**: 중첩된 JSON 구조로 한 번에 카테고리 트리 생성
- **O(n) 트리 조회**: Map 기반 부모-자식 관계 구성
- **중복 검증**: 같은 부모 아래 동일 이름 카테고리 방지

### 2. SKU 자동 생성 (Cartesian Product)

```
옵션 그룹: 색상[빨강, 파랑], 사이즈[S, M, L]
→ 생성되는 SKU: 6개 (2 x 3)
```

- `SkuGenerator`: 재귀 함수로 Cartesian Product 계산
- `ProductValidator`: SKU 개수 제한 검증 (최대 500개)
- SKU ID 형식: `{productCode}-{optionString}-{hash}`

### 3. Outbox 패턴 기반 이벤트 발행

```
UseCase (@Transactional)
  → Product 저장
  → IntegrationEventPublisher.publish()
  → CatalogOutboxEntry 저장 (같은 트랜잭션)
  → Debezium CDC가 Kafka로 발행
```

**트랜잭션 보장**: DB 저장과 이벤트 발행이 원자적으로 처리됨

## 이벤트 처리

### Published Events

| 이벤트 | Topic | 설명 |
|--------|-------|------|
| `ProductSkuCreatedEvent` | `koosco.commerce.product.default` | SKU 생성 시 발행 |

**Consumer**: inventory-service (재고 초기화)

### Consumed Events

없음 (Producer only)

## 도메인 모델

| 엔티티 | 설명 |
|--------|------|
| `Product` | 상품 (이름, 가격, 상태, 카테고리) |
| `ProductSku` | SKU (옵션 조합별 재고 단위) |
| `ProductOptionGroup` | 옵션 그룹 (색상, 사이즈 등) |
| `ProductOption` | 개별 옵션 (빨강, M 등) |
| `Category` | 계층형 카테고리 |
| `CatalogOutboxEntry` | Outbox 테이블 엔티티 |

**Value Objects**
- `ProductOptions`: 옵션 조합 비교용 (정규화된 Map)

## API 요약

### Product APIs

| Method | Path | 설명 |
|--------|------|------|
| GET | `/api/catalog/products` | 상품 목록 (페이징) |
| GET | `/api/catalog/products/{id}` | 상품 상세 |
| GET | `/api/catalog/products/{id}/skus` | SKU 조회 (옵션 조합) |
| POST | `/api/catalog/products` | 상품 생성 (인증 필수) |

### Category APIs

| Method | Path | 설명 |
|--------|------|------|
| GET | `/api/catalog/categories/tree` | 카테고리 트리 |
| POST | `/api/catalog/categories/tree` | 트리 생성 (재귀) |

**Swagger UI**: `http://localhost:8084/swagger-ui.html`

## 환경 설정

### 필수 환경 변수

```bash
DB_HOST=localhost
DB_PORT=3306
DB_NAME=commerce-catalog
DB_USERNAME=admin
DB_PASSWORD=admin1234
SPRING_KAFKA_BOOTSTRAP_SERVERS=localhost:9092
JWT_SECRET=mySecretKeyForJWT...
```

### 로컬 실행

```bash
# 인프라 실행
cd infra/docker && docker-compose up -d

# 서비스 실행
./gradlew :services:catalog-service:bootRun
```

## 주요 구현 포인트

1. **Cartesian Product 알고리즘**: 재귀 함수로 옵션 조합 생성
2. **도메인 검증**: SKU 개수 제한, 옵션 그룹 구조 검증
3. **Outbox 패턴**: Debezium CDC 연동으로 트랜잭션 일관성 보장
4. **계층형 데이터**: 재귀적 카테고리 생성/조회
