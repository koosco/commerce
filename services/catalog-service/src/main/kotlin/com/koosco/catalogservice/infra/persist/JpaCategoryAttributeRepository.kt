package com.koosco.catalogservice.infra.persist

import com.koosco.catalogservice.domain.entity.CategoryAttribute
import org.springframework.data.jpa.repository.JpaRepository

interface JpaCategoryAttributeRepository : JpaRepository<CategoryAttribute, Long> {

    fun findByCategoryIdOrderByOrderingAsc(categoryId: Long): List<CategoryAttribute>

    fun findByCategoryIdInOrderByOrderingAsc(categoryIds: List<Long>): List<CategoryAttribute>
}
