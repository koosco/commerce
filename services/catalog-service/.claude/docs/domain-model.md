# Catalog Service 도메인 모델

| 엔티티 | 설명 |
|--------|------|
| `Product` | 상품 (이름, 가격, 상태, 카테고리) |
| `ProductSku` | SKU (옵션 조합별 재고 단위) |
| `ProductOptionGroup` | 옵션 그룹 (색상, 사이즈 등) |
| `ProductOption` | 개별 옵션 (빨강, M 등) |
| `Category` | 계층형 카테고리 |
| `CatalogOutboxEntry` | Outbox 테이블 엔티티 |

## Value Objects

- `ProductOptions`: 옵션 조합 비교용 (정규화된 Map)
