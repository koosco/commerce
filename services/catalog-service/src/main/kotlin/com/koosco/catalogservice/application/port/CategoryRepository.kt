package com.koosco.catalogservice.application.port

import com.koosco.catalogservice.domain.entity.Category

interface CategoryRepository {

    fun save(category: Category): Category

    fun findByIdOrNull(id: Long): Category?

    fun findByParentIdOrderByOrderingAsc(parentId: Long?): List<Category>

    fun findByParentIsNull(): List<Category>

    fun findByDepthOrderByOrderingAsc(depth: Int): List<Category>

    fun findAllByOrderByDepthAscOrderingAsc(): List<Category>

    fun existsByNameAndParent(name: String, parent: Category?): Boolean
}
