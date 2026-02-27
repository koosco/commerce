package com.koosco.catalogservice.infra.persist

import com.koosco.catalogservice.application.port.ProductAttributeValueRepository
import com.koosco.catalogservice.domain.entity.ProductAttributeValue
import com.koosco.catalogservice.domain.entity.QProductAttributeValue.productAttributeValue
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProductAttributeValueRepositoryAdapter(
    private val jpaProductAttributeValueRepository: JpaProductAttributeValueRepository,
    private val queryFactory: JPAQueryFactory,
) : ProductAttributeValueRepository {

    override fun save(attributeValue: ProductAttributeValue): ProductAttributeValue =
        jpaProductAttributeValueRepository.save(attributeValue)

    override fun saveAll(attributeValues: List<ProductAttributeValue>): List<ProductAttributeValue> =
        jpaProductAttributeValueRepository.saveAll(attributeValues)

    override fun findByProductId(productId: Long): List<ProductAttributeValue> =
        jpaProductAttributeValueRepository.findByProductId(productId)

    override fun findByProductIdIn(productIds: List<Long>): List<ProductAttributeValue> =
        jpaProductAttributeValueRepository.findByProductIdIn(productIds)

    override fun findByProductIdAndAttributeId(productId: Long, attributeId: Long): ProductAttributeValue? =
        jpaProductAttributeValueRepository.findByProductIdAndAttributeId(productId, attributeId)

    @Transactional
    override fun deleteByProductId(productId: Long) = jpaProductAttributeValueRepository.deleteByProductId(productId)

    override fun findProductIdsByAttributeFilters(filters: Map<Long, String>): List<Long> {
        if (filters.isEmpty()) return emptyList()

        // Find product IDs that match ALL attribute filters (intersection)
        var productIds: Set<Long>? = null

        filters.forEach { (attributeId, value) ->
            val matchingProductIds = queryFactory
                .select(productAttributeValue.productId)
                .from(productAttributeValue)
                .where(
                    productAttributeValue.attributeId.eq(attributeId),
                    productAttributeValue.value.eq(value),
                )
                .fetch()
                .toSet()

            productIds = productIds?.intersect(matchingProductIds) ?: matchingProductIds
        }

        return productIds?.toList() ?: emptyList()
    }
}
