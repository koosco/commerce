package com.koosco.catalogservice.infra.persist

import com.koosco.catalogservice.domain.entity.Category
import org.springframework.data.jpa.repository.JpaRepository

interface JpaCategoryRepository : JpaRepository<Category, Long> {

    fun findByParentIdOrderByOrderingAsc(parentId: Long?): List<Category>

    fun findByParentIsNull(): List<Category>

    fun findByDepthOrderByOrderingAsc(depth: Int): List<Category>

    fun findAllByOrderByDepthAscOrderingAsc(): List<Category>

    fun existsByNameAndParent(name: String, parent: Category?): Boolean
}
