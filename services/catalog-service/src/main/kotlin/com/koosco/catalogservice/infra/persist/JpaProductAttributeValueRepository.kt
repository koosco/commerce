package com.koosco.catalogservice.infra.persist

import com.koosco.catalogservice.domain.entity.ProductAttributeValue
import org.springframework.data.jpa.repository.JpaRepository

interface JpaProductAttributeValueRepository : JpaRepository<ProductAttributeValue, Long> {

    fun findByProductId(productId: Long): List<ProductAttributeValue>

    fun findByProductIdIn(productIds: List<Long>): List<ProductAttributeValue>

    fun findByProductIdAndAttributeId(productId: Long, attributeId: Long): ProductAttributeValue?

    fun deleteByProductId(productId: Long)
}
