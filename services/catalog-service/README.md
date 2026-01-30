# Catalog Service

상품 카탈로그 및 카테고리 관리를 담당하는 서비스입니다.

## 목차

- [개요](#개요)
- [핵심 기능](#핵심-기능)
- [아키텍처](#아키텍처)
- [주요 기술 구현](#주요-기술-구현)
  - [계층형 카테고리 관리](#1-계층형-카테고리-관리)
  - [SKU 자동 생성 시스템](#2-sku-자동-생성-시스템)
  - [Outbox 패턴 기반 이벤트 발행](#3-outbox-패턴-기반-이벤트-발행)
  - [도메인 검증 로직](#4-도메인-검증-로직)
  - [옵션 조합 기반 SKU 검색](#5-옵션-조합-기반-sku-검색)
- [API 명세](#api-명세)
- [이벤트 발행](#이벤트-발행)
- [기술 스택](#기술-스택)
- [환경 설정](#환경-설정)
- [포트폴리오 주요 성과](#포트폴리오-주요-성과)

---

## 개요

Catalog Service는 전자상거래 시스템의 상품 정보와 카테고리 구조를 관리하는 핵심 서비스입니다.

**포트폴리오 관점의 주요 성과**

- **복잡한 도메인 모델링**: 상품-옵션-SKU 간의 다대다 관계를 효과적으로 설계
- **조합 알고리즘 구현**: Cartesian Product를 활용한 SKU 자동 생성 시스템
- **계층형 데이터 구조**: 재귀적 카테고리 트리 생성 및 조회 로직
- **이벤트 기반 통합**: Outbox 패턴을 활용한 안정적인 이벤트 발행
- **도메인 주도 설계**: 비즈니스 규칙을 도메인 엔티티 내부에 캡슐화

**서비스 정보**

- 포트: `8084`
- 데이터베이스: `commerce-catalog` (MariaDB)
- Kafka Topic: `koosco.commerce.product.default`

---

## 핵심 기능

### 1. 상품 관리 (Product Management)

- **상품 CRUD**: 생성, 조회, 수정, 삭제
- **옵션 그룹 관리**: 색상, 사이즈 등 다중 옵션 그룹 지원
- **SKU 자동 생성**: 옵션 조합에 따른 SKU 자동 생성
- **상품 검색**: 키워드, 카테고리 필터링 지원
- **페이징 지원**: Spring Data JPA 페이징

### 2. 카테고리 관리 (Category Management)

- **계층형 구조**: 무제한 깊이의 카테고리 트리
- **재귀적 생성**: 중첩된 JSON 구조로 한 번에 트리 생성 가능
- **중복 검증**: 같은 부모 아래 동일 이름 카테고리 방지
- **카테고리 코드 자동 생성**: 이름 기반 고유 코드 생성

### 3. SKU 조회 (SKU Finder)

- **옵션 조합 매칭**: 사용자가 선택한 옵션 조합으로 SKU 검색
- **가격 정보 제공**: 옵션별 추가 가격 반영
- **재고 연동**: SKU ID 기반 재고 조회 (inventory-service와 연동)

---

## 아키텍처

### Clean Architecture 구조

```
catalog-service/
├── api/                          # Presentation Layer
│   ├── ProductController         # 상품 REST API
│   ├── CategoryController        # 카테고리 REST API
│   ├── ProductRequests           # Request DTO
│   └── ProductResponses          # Response DTO
│
├── application/                  # Application Layer
│   ├── usecase/
│   │   ├── CreateProductUseCase      # 상품 생성 (SKU 자동 생성)
│   │   ├── GetProductListUseCase     # 상품 목록 조회
│   │   ├── FindSkuUseCase            # SKU 조회
│   │   └── CreateCategoryTreeUseCase # 카테고리 트리 생성
│   ├── command/                  # Command DTO
│   ├── result/                   # Result DTO
│   ├── port/
│   │   ├── ProductRepository         # Port 인터페이스
│   │   └── IntegrationEventPublisher # Port 인터페이스
│   └── contract/
│       └── outbound/
│           └── ProductSkuCreatedEvent  # Integration Event
│
├── domain/                       # Domain Layer
│   ├── entity/
│   │   ├── Product                   # 상품 엔티티
│   │   ├── ProductSku                # SKU 엔티티
│   │   ├── ProductOptionGroup        # 옵션 그룹
│   │   ├── ProductOption             # 개별 옵션
│   │   ├── Category                  # 카테고리 엔티티
│   │   └── CatalogOutboxEntry        # Outbox 엔티티
│   ├── service/
│   │   ├── SkuGenerator              # SKU 생성 도메인 서비스
│   │   └── ProductValidator          # 상품 검증 도메인 서비스
│   ├── vo/                       # Value Objects
│   └── enums/
│       └── ProductStatus             # 상품 상태
│
└── infra/                        # Infrastructure Layer
    ├── persist/
    │   ├── jpa/
    │   │   └── JpaProductRepository  # JPA Repository
    │   └── ProductRepositoryImpl     # Adapter 구현
    ├── messaging/kafka/producer/
    │   └── OutboxIntegrationEventPublisher  # Kafka Adapter
    └── outbox/
        └── CatalogOutboxRepository   # Outbox 저장소
```

### 의존성 흐름

```
api → application → domain
         ↓
      infra (구현체)
```

- **application/domain**은 **infra**에 의존하지 않음
- Port-Adapter 패턴을 통한 의존성 역전

---

## 주요 기술 구현

### 1. 계층형 카테고리 관리

#### 재귀적 카테고리 생성

**요구사항**: JSON 구조를 받아 한 번에 카테고리 트리 생성

```kotlin
// Category.kt
companion object {
    fun createTree(command: CreateCategoryTreeCommand): Category =
        createNodeRecursively(command, null)

    private fun createNodeRecursively(
        command: CreateCategoryTreeCommand,
        parent: Category?
    ): Category {
        parent?.hasNoDuplicateChild(command.name)

        val category = of(
            name = command.name,
            parent = parent,
            ordering = command.ordering,
        )

        parent?.addChild(category)

        // 재귀적으로 자식 생성
        command.children.forEach { childCommand ->
            createNodeRecursively(childCommand, category)
        }

        return category
    }
}
```

**핵심 포인트**

- **재귀 함수**로 중첩된 구조 처리
- **부모-자식 관계** 자동 설정
- **깊이(depth)** 자동 계산
- **중복 검증**을 도메인 엔티티에서 직접 수행

#### 카테고리 트리 조회

**요구사항**: 플랫한 리스트를 계층형 구조로 변환

```kotlin
// CategoryTreeBuilder.kt
fun build(categories: List<Category>): List<CategoryTreeInfo> {
    data class TreeNode(
        val id: Long,
        val name: String,
        val depth: Int,
        val parentId: Long?,
        val children: MutableList<TreeNode> = mutableListOf(),
    )

    // 1. 모든 노드를 Map으로 변환
    val nodeMap = categories
        .map { c -> TreeNode(c.id!!, c.name, c.depth, c.parent?.id) }
        .associateBy { it.id }

    // 2. 부모-자식 관계 설정
    nodeMap.values.forEach { node ->
        node.parentId?.let { parentId ->
            nodeMap[parentId]?.children?.add(node)
        }
    }

    // 3. 루트 노드만 반환
    return nodeMap.values.filter { it.parentId == null }
}
```

**핵심 포인트**

- **O(n)** 시간 복잡도로 트리 구성
- **Map** 자료구조를 활용한 빠른 탐색
- **불변 객체** 반환으로 안전성 보장

---

### 2. SKU 자동 생성 시스템

#### 문제 정의

**요구사항**: 상품 옵션 조합에 따라 모든 SKU를 자동 생성

**예시**

```
옵션 그룹:
- 색상: [빨강, 파랑, 검정]
- 사이즈: [S, M, L]

생성되는 SKU:
1. 빨강-S
2. 빨강-M
3. 빨강-L
4. 파랑-S
5. 파랑-M
6. 파랑-L
7. 검정-S
8. 검정-M
9. 검정-L

총 9개 SKU (3 x 3 = 9)
```

#### Cartesian Product 구현

```kotlin
// SkuGenerator.kt
fun generateSkus(product: Product) {
    if (product.optionGroups.isEmpty()) return

    // 1. 옵션 그룹별로 옵션 리스트 생성
    val options = product.optionGroups
        .sortedBy { it.ordering }
        .map { group ->
            group.options
                .sortedBy { it.ordering }
                .map { option -> group.name to option }
        }

    // 2. Cartesian Product 계산
    val combinations = cartesianProduct(options)

    // 3. SKU 생성
    val skus = combinations.map { combination ->
        val optionsMap = combination.associate { (groupName, option) ->
            groupName to option.name
        }
        val skuPrice = product.price +
            combination.sumOf { (_, option) -> option.additionalPrice }

        ProductSku.create(product, optionsMap, skuPrice)
    }

    product.addSkus(skus)
}

private fun <T> cartesianProduct(lists: List<List<T>>): List<List<T>> {
    if (lists.isEmpty()) return listOf(emptyList())
    if (lists.size == 1) return lists[0].map { listOf(it) }

    val result = mutableListOf<List<T>>()
    val rest = cartesianProduct(lists.drop(1))

    for (item in lists[0]) {
        for (r in rest) {
            result.add(listOf(item) + r)
        }
    }

    return result
}
```

**핵심 포인트**

- **재귀 함수**로 Cartesian Product 구현
- **가격 계산**: 기본 가격 + 옵션별 추가 가격
- **정렬 보장**: 옵션 그룹 및 옵션의 ordering 속성 활용

#### SKU ID 생성 규칙

```kotlin
// ProductSku.kt
fun generate(productCode: String, options: Map<String, String>): String {
    val optionString = options.entries
        .sortedBy { it.key }
        .joinToString("-") { it.value }

    val hash = optionString.hashCode().toString(16).uppercase()

    return "$productCode-$optionString-$hash"
}

// 예시: ELEC-20250125-AB3C-RED-M-3F2A1B4C
```

**구성 요소**

- **productCode**: 상품 고유 코드 (카테고리 기반)
- **optionString**: 옵션 조합 (정렬됨)
- **hash**: 옵션 조합의 해시값 (중복 방지)

---

### 3. Outbox 패턴 기반 이벤트 발행

#### 트랜잭셔널 아웃박스 패턴

**문제점**: 상품 생성과 이벤트 발행이 원자적(atomic)으로 이루어지지 않으면 데이터 불일치 발생

**해결 방법**: Outbox 패턴 + Debezium CDC

```kotlin
// OutboxIntegrationEventPublisher.kt
@Component
class OutboxIntegrationEventPublisher(
    private val outboxRepository: CatalogOutboxRepository,
    private val topicResolver: KafkaTopicResolver,
    private val objectMapper: ObjectMapper,
    @Value("\${spring.application.name}") private val source: String,
) : IntegrationEventPublisher {

    override fun publish(event: ProductIntegrationEvent) {
        val cloudEvent = event.toCloudEvent(source)
        val topic = topicResolver.resolve(event)
        val partitionKey = event.getPartitionKey()
        val eventType = event.getEventType()

        val payload = objectMapper.writeValueAsString(cloudEvent)

        // Outbox 테이블에 저장 (트랜잭션 내)
        val outboxEntry = CatalogOutboxEntry.create(
            aggregateId = event.skuId,
            eventType = eventType,
            payload = payload,
            topic = topic,
            partitionKey = partitionKey,
        )

        outboxRepository.save(outboxEntry)

        logger.info("Outbox entry saved: type=$eventType, skuId=${event.skuId}")
    }
}
```

#### 이벤트 발행 흐름

```
1. UseCase 내에서 Product 저장 (@Transactional)
   ↓
2. IntegrationEventPublisher.publish() 호출
   ↓
3. CatalogOutboxEntry 저장 (같은 트랜잭션)
   ↓
4. 트랜잭션 커밋
   ↓
5. Debezium CDC가 catalog_outbox 테이블 변경 감지
   ↓
6. Kafka로 이벤트 발행
```

**핵심 포인트**

- **트랜잭션 보장**: DB 저장과 이벤트 발행이 원자적
- **재시도 불필요**: Debezium이 최소 한 번 전송 보장
- **멱등성**: Outbox 테이블을 통해 중복 발행 방지

#### 이벤트 스키마

```kotlin
// ProductSkuCreatedEvent.kt
data class ProductSkuCreatedEvent(
    override val skuId: String,         // 파티션 키
    val productId: Long,
    val productCode: String,
    val price: Long,
    val optionValues: String,           // JSON 형태의 옵션 조합
    val initialQuantity: Int = 0,
    val createdAt: LocalDateTime,
) : ProductIntegrationEvent {
    override fun getEventType(): String = "product.sku.created"
}
```

**inventory-service가 이 이벤트를 소비**하여 재고를 초기화합니다.

---

### 4. 도메인 검증 로직

#### SKU 생성 제한 검증

**문제점**: 옵션 조합 폭발로 인한 성능 저하

```kotlin
// ProductValidator.kt
@Service
class ProductValidator {
    companion object {
        private const val MAX_OPTION_GROUPS = 5
        private const val MAX_OPTIONS_PER_GROUP = 20
        private const val MAX_SKU_COUNT = 500
        private const val RECOMMENDED_MAX_SKU_COUNT = 100
    }

    fun validateSkuCount(optionGroupSpecs: List<OptionGroupCreateSpec>) {
        // 1. 옵션 그룹 개수 검증
        require(optionGroupSpecs.size <= MAX_OPTION_GROUPS) {
            "옵션 그룹은 최대 ${MAX_OPTION_GROUPS}개까지만 생성할 수 있습니다."
        }

        // 2. 각 그룹의 옵션 개수 검증
        optionGroupSpecs.forEach { group ->
            require(group.options.size <= MAX_OPTIONS_PER_GROUP) {
                "옵션 그룹 '${group.name}'의 옵션은 최대 ${MAX_OPTIONS_PER_GROUP}개까지만 생성할 수 있습니다."
            }
        }

        // 3. 예상 SKU 개수 계산
        val expectedSkuCount = optionGroupSpecs
            .map { it.options.size }
            .reduce { acc, size -> acc * size }

        // 4. SKU 개수 검증
        require(expectedSkuCount <= MAX_SKU_COUNT) {
            "생성 가능한 SKU 개수가 제한을 초과합니다. (예상: ${expectedSkuCount}개)"
        }

        // 5. 권장 개수 초과 시 경고
        if (expectedSkuCount > RECOMMENDED_MAX_SKU_COUNT) {
            logger.warn("생성되는 SKU 개수가 권장 개수를 초과합니다.")
        }
    }
}
```

**핵심 포인트**

- **사전 검증**: SKU 생성 전 개수 계산
- **비즈니스 제약**: 실무적인 제한 사항 적용
- **명확한 에러 메시지**: 사용자에게 구체적인 피드백 제공

#### 옵션 그룹 구조 검증

```kotlin
fun validateOptionGroupStructure(optionGroupSpecs: List<OptionGroupCreateSpec>) {
    val suspiciousNames = listOf(
        "RED", "BLUE", "BLACK", "WHITE", "GRAY", "GREEN",
        "XS", "S", "M", "L", "XL", "XXL",
    )

    optionGroupSpecs.forEach { group ->
        if (group.name.uppercase() in suspiciousNames) {
            logger.warn(
                "옵션 그룹 이름 '${group.name}'이 의심스럽습니다. " +
                "옵션 그룹 이름은 '색상', '사이즈' 등의 카테고리명이어야 합니다."
            )
        }
    }
}
```

**핵심 포인트**

- **일반적인 실수 방지**: 옵션 값을 그룹 이름으로 사용하는 오류 감지
- **개발자 경험 개선**: 명확한 가이드 제공

---

### 5. 옵션 조합 기반 SKU 검색

**요구사항**: 사용자가 선택한 옵션 조합으로 정확한 SKU 찾기

```kotlin
// FindSkuUseCase.kt
@UseCase
class FindSkuUseCase(private val productRepository: ProductRepository) {
    @Transactional(readOnly = true)
    fun execute(command: FindSkuCommand): ProductSku {
        val product = productRepository.findOrNull(command.productId)
            ?: throw IllegalArgumentException("Product not found")

        // 입력받은 옵션을 VO로 변환
        val requestedOptions = ProductOptions.from(command.options)

        // SKU 옵션과 객체 비교
        val sku = product.skus.find { sku ->
            val skuOptions = ProductOptions.fromJson(sku.optionValues)
            skuOptions == requestedOptions
        } ?: throw IllegalArgumentException("No SKU found for options")

        return sku
    }
}
```

**ProductOptions VO (Value Object)**

```kotlin
data class ProductOptions(private val options: Map<String, String>) {
    // 정규화: 키 정렬로 일관된 비교 보장
    private val normalized: Map<String, String> = options.entries
        .sortedBy { it.key }
        .associate { it.key to it.value }

    override fun equals(other: Any?): Boolean {
        if (other !is ProductOptions) return false
        return normalized == other.normalized
    }

    override fun hashCode(): Int = normalized.hashCode()
}
```

**비교 예시**

```kotlin
val options1 = ProductOptions.from(mapOf("색상" to "빨강", "사이즈" to "M"))
val options2 = ProductOptions.from(mapOf("사이즈" to "M", "색상" to "빨강"))

options1 == options2  // true (순서 무관)
```

---

## API 명세

### Product APIs

| Method | Path | 설명 | 인증 |
|--------|------|------|------|
| GET | `/api/products` | 상품 목록 조회 (페이징, 필터링) | 불필요 |
| GET | `/api/products/{productId}` | 상품 상세 조회 | 불필요 |
| GET | `/api/products/{productId}/skus` | SKU 조회 (옵션 조합) | 불필요 |
| POST | `/api/products` | 상품 생성 (SKU 자동 생성) | 필수 |
| PUT | `/api/products/{productId}` | 상품 수정 | 필수 |
| DELETE | `/api/products/{productId}` | 상품 삭제 (논리 삭제) | 필수 |

### Category APIs

| Method | Path | 설명 | 인증 |
|--------|------|------|------|
| GET | `/api/categories` | 카테고리 리스트 조회 | 불필요 |
| GET | `/api/categories/tree` | 카테고리 트리 조회 | 불필요 |
| POST | `/api/categories` | 카테고리 생성 | 필수 |
| POST | `/api/categories/tree` | 카테고리 트리 생성 (재귀) | 필수 |

### Request 예시

#### 상품 생성

```json
POST /api/products
{
  "name": "클래식 티셔츠",
  "description": "편안한 코튼 소재",
  "price": 29000,
  "status": "ACTIVE",
  "categoryId": 1,
  "thumbnailImageUrl": "https://example.com/image.jpg",
  "brand": "MyBrand",
  "optionGroups": [
    {
      "name": "색상",
      "ordering": 1,
      "options": [
        { "name": "빨강", "additionalPrice": 0, "ordering": 1 },
        { "name": "파랑", "additionalPrice": 0, "ordering": 2 },
        { "name": "검정", "additionalPrice": 0, "ordering": 3 }
      ]
    },
    {
      "name": "사이즈",
      "ordering": 2,
      "options": [
        { "name": "S", "additionalPrice": 0, "ordering": 1 },
        { "name": "M", "additionalPrice": 0, "ordering": 2 },
        { "name": "L", "additionalPrice": 1000, "ordering": 3 }
      ]
    }
  ]
}
```

**결과**: 9개의 SKU 자동 생성 (3색상 x 3사이즈)

#### SKU 조회

```
GET /api/products/1/skus?색상=빨강&사이즈=M

Response:
{
  "skuId": "ELEC-20250125-AB3C-빨강-M-3F2A1B4C",
  "productId": 1,
  "price": 29000,
  "optionValues": "{\"색상\":\"빨강\",\"사이즈\":\"M\"}"
}
```

#### 카테고리 트리 생성

```json
POST /api/categories/tree
{
  "name": "전자제품",
  "ordering": 1,
  "children": [
    {
      "name": "컴퓨터",
      "ordering": 1,
      "children": [
        { "name": "노트북", "ordering": 1, "children": [] },
        { "name": "데스크탑", "ordering": 2, "children": [] }
      ]
    },
    {
      "name": "휴대폰",
      "ordering": 2,
      "children": []
    }
  ]
}
```

**결과**: 재귀적으로 전체 트리 생성

---

## 이벤트 발행

### ProductSkuCreatedEvent

**Topic**: `koosco.commerce.product.default`

**Schema**: CloudEvent 표준

```json
{
  "specversion": "1.0",
  "type": "product.sku.created",
  "source": "catalog-service",
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "time": "2025-01-25T12:00:00Z",
  "datacontenttype": "application/json",
  "data": {
    "skuId": "ELEC-20250125-AB3C-RED-M-3F2A1B4C",
    "productId": 1,
    "productCode": "ELEC-20250125-AB3C",
    "price": 29000,
    "optionValues": "{\"색상\":\"빨강\",\"사이즈\":\"M\"}",
    "initialQuantity": 0,
    "createdAt": "2025-01-25T12:00:00"
  }
}
```

**소비자**: inventory-service

- SKU 생성 이벤트를 받아 재고 초기화
- `skuId`를 기반으로 재고 레코드 생성

**발행 패턴**

```kotlin
@UseCase
class CreateProductUseCase(
    private val productRepository: ProductRepository,
    private val skuGenerator: SkuGenerator,
    private val integrationEventPublisher: IntegrationEventPublisher,
) {
    @Transactional
    fun execute(command: CreateProductCommand): ProductInfo {
        // 1. 상품 생성
        val product = Product.create(...)

        // 2. SKU 생성
        skuGenerator.generateSkus(product)

        // 3. 저장
        val savedProduct = productRepository.save(product)

        // 4. Integration Event 발행 (Outbox 패턴)
        product.skus.forEach { sku ->
            integrationEventPublisher.publish(
                ProductSkuCreatedEvent(
                    skuId = sku.skuId,
                    productId = savedProduct.id!!,
                    productCode = product.productCode,
                    price = sku.price,
                    optionValues = sku.optionValues,
                    initialQuantity = 0,
                    createdAt = LocalDateTime.now(),
                )
            )
        }

        return ProductInfo.from(savedProduct)
    }
}
```

---

## 기술 스택

| 분류 | 기술 |
|------|------|
| 언어 | Kotlin 1.9.25 |
| 프레임워크 | Spring Boot 3.5.8 |
| 데이터베이스 | MariaDB |
| ORM | JPA (Hibernate), QueryDSL 5.0.0 |
| 메시징 | Apache Kafka, Spring Kafka |
| CDC | Debezium (Outbox 패턴) |
| 문서화 | Springdoc OpenAPI 3 |
| 모니터링 | Spring Actuator, Prometheus |
| 테스트 | JUnit 5, Testcontainers |

---

## 환경 설정

### 필수 환경 변수

```bash
# 데이터베이스
DB_HOST=localhost
DB_PORT=3306
DB_NAME=commerce-catalog
DB_USERNAME=admin
DB_PASSWORD=admin1234

# Kafka
SPRING_KAFKA_BOOTSTRAP_SERVERS=localhost:9092

# JWT (인증 필터용)
JWT_SECRET=mySecretKeyForJWTWhichShouldBeAtLeast256BitsLongToEnsureSecurityAndCompliance
JWT_EXPIRATION=86400
```

### 로컬 실행

#### 1. 인프라 실행 (DB, Kafka)

```bash
cd infra/docker
docker-compose up -d
```

#### 2. 서비스 실행

```bash
# 프로젝트 루트에서
./gradlew :services:catalog-service:bootRun

# 또는 IDE에서 CatalogServiceApplication.kt 실행
```

#### 3. API 문서 확인

```
http://localhost:8084/swagger-ui.html
```

### Docker 이미지 빌드

```bash
# 1. JAR 빌드
./gradlew :services:catalog-service:build

# 2. Docker 이미지 빌드
cd services/catalog-service
docker build -t catalog-service:latest .
```

---

## 포트폴리오 주요 성과

### 1. 알고리즘 구현

- **Cartesian Product**: 재귀 함수를 활용한 조합 생성
- **재귀적 트리 구조**: 계층형 데이터 생성 및 조회
- **시간 복잡도 최적화**: O(n) 트리 구성 알고리즘

### 2. 도메인 주도 설계

- **비즈니스 규칙 캡슐화**: 도메인 엔티티 내부에 검증 로직 포함
- **풍부한 도메인 모델**: 단순 데이터 저장소가 아닌 비즈니스 로직 포함
- **Factory 패턴**: 복잡한 객체 생성 로직 분리

### 3. 이벤트 기반 아키텍처

- **Outbox 패턴**: 트랜잭션 일관성 보장
- **CloudEvent 표준**: 이벤트 스키마 표준화
- **Debezium CDC**: 신뢰성 있는 이벤트 발행

### 4. Clean Architecture

- **의존성 역전**: Port-Adapter 패턴
- **계층 간 명확한 분리**: api/application/domain/infra
- **테스트 가능성**: 도메인 로직 단위 테스트

### 5. 성능 고려사항

- **N+1 문제 해결**: Fetch Join 활용
- **페이징 처리**: 대량 데이터 조회 최적화
- **인덱스 전략**: Outbox 테이블 인덱스 설계

---

## 개선 고려 사항

### 1. 성능 최적화

- **QueryDSL 동적 쿼리**: 복잡한 상품 검색 조건 최적화
- **캐싱**: 자주 조회되는 카테고리 트리 Redis 캐싱

### 2. 기능 확장

- **재고 동기화**: inventory-service와 실시간 재고 조회
- **검색 엔진 통합**: Elasticsearch 연동 (전문 검색)
- **이미지 관리**: S3 연동 및 CDN 적용

### 3. 운영 관점

- **모니터링**: SKU 생성 시간, 이벤트 발행 지연 시간 메트릭
- **알림**: Outbox 처리 실패 시 Slack 알림
- **테스트**: 조합 폭발 시나리오 성능 테스트

---

## 참고 자료

- **Clean Architecture**: Robert C. Martin
- **Domain-Driven Design**: Eric Evans
- **Outbox Pattern**: Chris Richardson (Microservices Patterns)
- **CloudEvents Specification**: https://cloudevents.io
- **Debezium Documentation**: https://debezium.io

---

## 문의

프로젝트 관련 문의사항은 GitHub Issues를 통해 남겨주세요.
