# mono QueryDSL Usage

이 skill은 QueryDSL 레포지토리 작성을 참조합니다.

## 사용 시점

- 복잡한 동적 쿼리 작성이 필요할 때
- CustomRepository 패턴 적용이 필요할 때
- Q클래스 생성 및 사용이 필요할 때
- Projection 사용이 필요할 때

## 의존성 설정

### build.gradle.kts

```kotlin
plugins {
    kotlin("kapt")  // kapt 플러그인 필요
}

dependencies {
    // QueryDSL
    implementation("com.querydsl:querydsl-jpa:5.1.0:jakarta")
    kapt("com.querydsl:querydsl-apt:5.1.0:jakarta")
    kapt("jakarta.annotation:jakarta.annotation-api")
    kapt("jakarta.persistence:jakarta.persistence-api")
}

// kapt 설정 (Q클래스 생성 위치)
kapt {
    arguments {
        arg("querydsl.entityAccessors", "true")
    }
}
```

## Quick Reference

### 1. Q클래스 생성

```bash
# kapt를 통해 Q클래스 생성
./gradlew :services:order-service:kaptKotlin

# 생성 위치
# build/generated/source/kapt/main/com/koosco/.../QEntity.kt
```

### 2. CustomRepository 패턴

**Repository 인터페이스 구조:**

```kotlin
// 1. Spring Data Repository
interface OrderJpaRepository : JpaRepository<OrderEntity, String>, OrderCustomRepository

// 2. Custom Repository Interface
interface OrderCustomRepository {
    fun findByConditions(condition: OrderSearchCondition): List<OrderEntity>
    fun findWithItemsById(orderId: String): OrderEntity?
}

// 3. Custom Repository Implementation
class OrderCustomRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : OrderCustomRepository {

    override fun findByConditions(condition: OrderSearchCondition): List<OrderEntity> {
        return queryFactory
            .selectFrom(orderEntity)
            .where(
                userIdEq(condition.userId),
                statusIn(condition.statuses),
                createdAtBetween(condition.startDate, condition.endDate),
            )
            .orderBy(orderEntity.createdAt.desc())
            .fetch()
    }

    override fun findWithItemsById(orderId: String): OrderEntity? {
        return queryFactory
            .selectFrom(orderEntity)
            .leftJoin(orderEntity.items, orderItemEntity).fetchJoin()
            .where(orderEntity.id.eq(orderId))
            .fetchOne()
    }

    // BooleanExpression 헬퍼 메서드
    private fun userIdEq(userId: String?): BooleanExpression? {
        return userId?.let { orderEntity.userId.eq(it) }
    }

    private fun statusIn(statuses: List<OrderStatus>?): BooleanExpression? {
        return if (statuses.isNullOrEmpty()) null
        else orderEntity.status.`in`(statuses)
    }

    private fun createdAtBetween(start: Instant?, end: Instant?): BooleanExpression? {
        return when {
            start != null && end != null -> orderEntity.createdAt.between(start, end)
            start != null -> orderEntity.createdAt.goe(start)
            end != null -> orderEntity.createdAt.loe(end)
            else -> null
        }
    }
}
```

### 3. JPAQueryFactory 설정

```kotlin
@Configuration
class QueryDslConfig {

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    @Bean
    fun jpaQueryFactory(): JPAQueryFactory {
        return JPAQueryFactory(entityManager)
    }
}
```

### 4. 검색 조건 클래스

```kotlin
data class OrderSearchCondition(
    val userId: String? = null,
    val statuses: List<OrderStatus>? = null,
    val startDate: Instant? = null,
    val endDate: Instant? = null,
)
```

### 5. Projection 사용

**DTO Projection:**

```kotlin
data class OrderSummaryDto(
    val orderId: String,
    val userId: String,
    val totalAmount: BigDecimal,
    val status: OrderStatus,
)

// Query
fun findOrderSummaries(userId: String): List<OrderSummaryDto> {
    return queryFactory
        .select(
            Projections.constructor(
                OrderSummaryDto::class.java,
                orderEntity.id,
                orderEntity.userId,
                orderEntity.totalAmount,
                orderEntity.status,
            )
        )
        .from(orderEntity)
        .where(orderEntity.userId.eq(userId))
        .fetch()
}
```

**Tuple Projection:**

```kotlin
fun findOrderStats(): List<Tuple> {
    return queryFactory
        .select(
            orderEntity.status,
            orderEntity.count(),
            orderEntity.totalAmount.sum(),
        )
        .from(orderEntity)
        .groupBy(orderEntity.status)
        .fetch()
}
```

### 6. 페이징 처리

```kotlin
fun findOrdersWithPaging(
    condition: OrderSearchCondition,
    pageable: Pageable,
): Page<OrderEntity> {
    val content = queryFactory
        .selectFrom(orderEntity)
        .where(
            userIdEq(condition.userId),
            statusIn(condition.statuses),
        )
        .offset(pageable.offset)
        .limit(pageable.pageSize.toLong())
        .orderBy(orderEntity.createdAt.desc())
        .fetch()

    val total = queryFactory
        .select(orderEntity.count())
        .from(orderEntity)
        .where(
            userIdEq(condition.userId),
            statusIn(condition.statuses),
        )
        .fetchOne() ?: 0L

    return PageImpl(content, pageable, total)
}
```

### 7. 서브쿼리

```kotlin
fun findOrdersWithMaxAmount(userId: String): List<OrderEntity> {
    val maxAmountSubQuery = JPAExpressions
        .select(orderEntity.totalAmount.max())
        .from(orderEntity)
        .where(orderEntity.userId.eq(userId))

    return queryFactory
        .selectFrom(orderEntity)
        .where(
            orderEntity.userId.eq(userId),
            orderEntity.totalAmount.eq(maxAmountSubQuery),
        )
        .fetch()
}
```

### 8. Join 쿼리

```kotlin
fun findOrdersWithPayment(): List<OrderEntity> {
    return queryFactory
        .selectFrom(orderEntity)
        .leftJoin(orderEntity.payment, paymentEntity).fetchJoin()
        .where(orderEntity.status.eq(OrderStatus.PAID))
        .fetch()
}
```

## Q클래스 Import

```kotlin
import com.koosco.orderservice.order.infra.persist.entity.QOrderEntity.orderEntity
import com.koosco.orderservice.order.infra.persist.entity.QOrderItemEntity.orderItemEntity
```

## 자주 발생하는 문제

### Q클래스가 없을 때

```bash
# kapt 재실행
./gradlew clean :services:order-service:kaptKotlin
```

### fetchJoin과 페이징

fetchJoin과 페이징을 함께 사용하면 메모리에서 페이징이 발생합니다:

```kotlin
// ❌ 피해야 함 - 메모리 페이징
queryFactory
    .selectFrom(orderEntity)
    .leftJoin(orderEntity.items).fetchJoin()
    .offset(pageable.offset)
    .limit(pageable.pageSize.toLong())

// ✅ 권장 - ID 먼저 조회 후 fetchJoin
val ids = queryFactory
    .select(orderEntity.id)
    .from(orderEntity)
    .offset(pageable.offset)
    .limit(pageable.pageSize.toLong())
    .fetch()

val orders = queryFactory
    .selectFrom(orderEntity)
    .leftJoin(orderEntity.items).fetchJoin()
    .where(orderEntity.id.`in`(ids))
    .fetch()
```
