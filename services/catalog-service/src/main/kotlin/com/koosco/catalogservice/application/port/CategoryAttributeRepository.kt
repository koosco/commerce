package com.koosco.catalogservice.application.port

import com.koosco.catalogservice.domain.entity.CategoryAttribute

interface CategoryAttributeRepository {

    fun save(attribute: CategoryAttribute): CategoryAttribute

    fun findOrNull(id: Long): CategoryAttribute?

    fun findByCategoryId(categoryId: Long): List<CategoryAttribute>

    fun findByCategoryIdIn(categoryIds: List<Long>): List<CategoryAttribute>

    fun delete(attribute: CategoryAttribute)
}
