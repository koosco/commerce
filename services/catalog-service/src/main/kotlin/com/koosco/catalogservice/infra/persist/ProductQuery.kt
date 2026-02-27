package com.koosco.catalogservice.infra.persist

import com.koosco.catalogservice.application.command.GetProductListCommand
import com.koosco.catalogservice.application.port.ProductAttributeValueRepository
import com.koosco.catalogservice.domain.entity.Product
import com.koosco.catalogservice.domain.entity.QBrand.brand
import com.koosco.catalogservice.domain.entity.QCategory.category
import com.koosco.catalogservice.domain.entity.QProduct.product
import com.koosco.catalogservice.domain.enums.ProductStatus
import com.koosco.catalogservice.domain.enums.SortStrategy
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.CaseBuilder
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.core.types.dsl.NumberExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Repository

@Repository
class ProductQuery(
    private val queryFactory: JPAQueryFactory,
    private val productAttributeValueRepository: ProductAttributeValueRepository,
) {

    fun search(command: GetProductListCommand): Page<Product> {
        val where = buildWhere(command)

        val query = queryFactory
            .selectFrom(product)
            .where(*where.toTypedArray())

        applySortOrder(query, command)

        val content = query
            .offset(command.pageable.offset)
            .limit(command.pageable.pageSize.toLong())
            .fetch()

        val countQuery = queryFactory
            .select(product.count())
            .from(product)
            .where(*where.toTypedArray())

        return PageableExecutionUtils.getPage(content, command.pageable) {
            countQuery.fetchOne() ?: 0L
        }
    }

    private fun buildWhere(command: GetProductListCommand): List<BooleanExpression> {
        val baseConditions = listOfNotNull(
            product.status.eq(ProductStatus.ACTIVE),
            command.categoryId?.let { product.categoryId.eq(it) },
            command.brandId?.let { product.brandId.eq(it) },
            command.keyword?.takeIf { it.isNotBlank() }?.let {
                product.name.containsIgnoreCase(it)
                    .or(product.description.containsIgnoreCase(it))
            },
            command.minPrice?.let { product.price.goe(it) },
            command.maxPrice?.let { product.price.loe(it) },
        )

        if (command.attributeFilters.isEmpty()) {
            return baseConditions
        }

        val matchingProductIds = productAttributeValueRepository
            .findProductIdsByAttributeFilters(command.attributeFilters)

        if (matchingProductIds.isEmpty()) {
            return baseConditions + product.id.eq(-1L)
        }

        return baseConditions + product.id.`in`(matchingProductIds)
    }

    private fun applySortOrder(query: com.querydsl.jpa.impl.JPAQuery<Product>, command: GetProductListCommand) {
        when (command.sort) {
            SortStrategy.RECOMMENDED -> applyRecommendedSort(query, command)
            SortStrategy.LATEST -> query.orderBy(product.createdAt.desc())
            SortStrategy.PRICE_ASC -> query.orderBy(product.price.asc())
            SortStrategy.PRICE_DESC -> query.orderBy(product.price.desc())
            SortStrategy.POPULARITY -> query.orderBy(popularityScore().desc())
            SortStrategy.BEST_SELLING -> query.orderBy(product.salesCount.desc(), product.createdAt.desc())
            SortStrategy.RATING_DESC -> query.orderBy(product.averageRating.desc(), product.reviewCount.desc())
            SortStrategy.REVIEW_COUNT_DESC -> query.orderBy(product.reviewCount.desc(), product.averageRating.desc())
        }
    }

    /**
     * RECOMMENDED: 카테고리/브랜드명 매칭 부스팅 + 인기도 + 최신순 복합 정렬
     */
    private fun applyRecommendedSort(query: com.querydsl.jpa.impl.JPAQuery<Product>, command: GetProductListCommand) {
        val keyword = command.keyword?.takeIf { it.isNotBlank() }

        if (keyword != null) {
            val categoryBoost: NumberExpression<Int> = CaseBuilder()
                .`when`(
                    product.categoryId.`in`(
                        queryFactory.select(category.id)
                            .from(category)
                            .where(category.name.containsIgnoreCase(keyword)),
                    ),
                ).then(1)
                .otherwise(0)

            val brandBoost: NumberExpression<Int> = CaseBuilder()
                .`when`(
                    product.brandId.`in`(
                        queryFactory.select(brand.id)
                            .from(brand)
                            .where(brand.name.containsIgnoreCase(keyword)),
                    ),
                ).then(1)
                .otherwise(0)

            val nameBoost: NumberExpression<Int> = CaseBuilder()
                .`when`(product.name.containsIgnoreCase(keyword)).then(1)
                .otherwise(0)

            val relevanceScore = categoryBoost.add(brandBoost).add(nameBoost)

            query.orderBy(
                relevanceScore.desc(),
                popularityScore().desc(),
                product.createdAt.desc(),
            )
        } else {
            query.orderBy(
                popularityScore().desc(),
                product.createdAt.desc(),
            )
        }
    }

    /**
     * 인기도 점수 계산:
     * salesCount * 0.4 + (averageRating / 5.0) * 0.3 + recencyScore * 0.3
     *
     * recencyScore: 최근 30일 이내 생성된 상품일수록 높은 점수 (0.0 ~ 1.0)
     * QueryDSL에서 날짜 계산이 제한적이므로 recencyScore는 고정 상수(0.5)로 근사한다.
     */
    private fun popularityScore(): NumberExpression<Double> {
        val salesScore = product.salesCount.doubleValue().multiply(WEIGHT_SALES)
        val ratingScore = product.averageRating.divide(MAX_RATING).multiply(WEIGHT_RATING)
        val recencyScore = Expressions.asNumber(DEFAULT_RECENCY_SCORE).multiply(WEIGHT_RECENCY)
        return salesScore.add(ratingScore).add(recencyScore)
    }

    companion object {
        private const val WEIGHT_SALES = 0.4
        private const val WEIGHT_RATING = 0.3
        private const val WEIGHT_RECENCY = 0.3
        private const val MAX_RATING = 5.0
        private const val DEFAULT_RECENCY_SCORE = 0.5
    }
}
