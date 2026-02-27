package com.koosco.catalogservice.infra.persist

import com.koosco.catalogservice.application.command.GetProductListCommand
import com.koosco.catalogservice.application.command.ProductSortType
import com.koosco.catalogservice.application.port.ProductAttributeValueRepository
import com.koosco.catalogservice.domain.entity.Product
import com.koosco.catalogservice.domain.entity.QProduct.product
import com.koosco.catalogservice.domain.enums.ProductStatus
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.dsl.BooleanExpression
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

        val content = queryFactory
            .selectFrom(product)
            .where(*where.toTypedArray())
            .orderBy(sortOrder(command.sort))
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
            // No products match the attribute filters, return impossible condition
            return baseConditions + product.id.eq(-1L)
        }

        return baseConditions + product.id.`in`(matchingProductIds)
    }

    private fun sortOrder(sort: ProductSortType): OrderSpecifier<*> = when (sort) {
        ProductSortType.LATEST -> product.createdAt.desc()
        ProductSortType.PRICE_ASC -> product.price.asc()
        ProductSortType.PRICE_DESC -> product.price.desc()
        ProductSortType.RATING_DESC -> product.averageRating.desc()
        ProductSortType.REVIEW_COUNT_DESC -> product.reviewCount.desc()
    }
}
