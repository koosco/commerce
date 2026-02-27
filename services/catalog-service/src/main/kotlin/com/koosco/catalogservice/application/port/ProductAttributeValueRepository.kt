package com.koosco.catalogservice.application.port

import com.koosco.catalogservice.domain.entity.ProductAttributeValue

interface ProductAttributeValueRepository {

    fun save(attributeValue: ProductAttributeValue): ProductAttributeValue

    fun saveAll(attributeValues: List<ProductAttributeValue>): List<ProductAttributeValue>

    fun findByProductId(productId: Long): List<ProductAttributeValue>

    fun findByProductIdIn(productIds: List<Long>): List<ProductAttributeValue>

    fun findByProductIdAndAttributeId(productId: Long, attributeId: Long): ProductAttributeValue?

    fun deleteByProductId(productId: Long)

    fun findProductIdsByAttributeFilters(filters: Map<Long, String>): List<Long>
}
