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
        val baseConditions = mutableListOf<BooleanExpression>(
            product.status.eq(ProductStatus.ACTIVE),
        )

        command.categoryId?.let { baseConditions.add(product.categoryId.eq(it)) }
        command.brandId?.let { baseConditions.add(product.brandId.eq(it)) }
        command.keyword?.takeIf { it.isNotBlank() }?.let {
            baseConditions.add(
                product.name.containsIgnoreCase(it)
                    .or(product.description.containsIgnoreCase(it)),
            )
        }
        command.minPrice?.let { baseConditions.add(product.price.goe(it)) }
        command.maxPrice?.let { baseConditions.add(product.price.loe(it)) }

        if (command.attributeFilters.isNotEmpty()) {
            val matchingProductIds = productAttributeValueRepository
                .findProductIdsByAttributeFilters(command.attributeFilters)

            if (matchingProductIds.isEmpty()) {
                baseConditions.add(product.id.eq(-1L))
            } else {
                baseConditions.add(product.id.`in`(matchingProductIds))
            }
        }

        return baseConditions
    }

    private fun sortOrder(sort: ProductSortType): OrderSpecifier<*> = when (sort) {
        ProductSortType.LATEST -> product.createdAt.desc()
        ProductSortType.PRICE_ASC -> product.price.asc()
        ProductSortType.PRICE_DESC -> product.price.desc()
    }
}
