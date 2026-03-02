package com.koosco.catalogservice.infra.persist

import com.koosco.catalogservice.domain.entity.Category
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface JpaCategoryRepository : JpaRepository<Category, Long> {

    fun findByParentIdOrderByOrderingAsc(parentId: Long?): List<Category>

    fun findByParentIsNull(): List<Category>

    fun findByDepthOrderByOrderingAsc(depth: Int): List<Category>

    fun findAllByOrderByDepthAscOrderingAsc(): List<Category>

    fun existsByNameAndParent(name: String, parent: Category?): Boolean

    @Query(
        value = """
            WITH RECURSIVE descendants AS (
                SELECT id FROM categories WHERE id = :categoryId
                UNION ALL
                SELECT c.id FROM categories c
                INNER JOIN descendants d ON c.parent_id = d.id
            )
            SELECT id FROM descendants
        """,
        nativeQuery = true,
    )
    fun findDescendantIds(categoryId: Long): List<Long>
}
