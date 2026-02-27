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
     * viewCount * 1 + orderCount * 3 으로 인기도 점수 계산
     */
    private fun popularityScore(): NumberExpression<Long> = product.viewCount.add(product.orderCount.multiply(3))
}
