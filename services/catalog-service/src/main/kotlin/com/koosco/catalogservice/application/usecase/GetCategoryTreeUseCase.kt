package com.koosco.catalogservice.application.usecase

import com.koosco.catalogservice.application.dto.CategoryTreeInfo
import com.koosco.catalogservice.application.dto.CategoryTreeNode
import com.koosco.catalogservice.application.port.CategoryRepository
import com.koosco.catalogservice.domain.entity.Category
import com.koosco.common.core.annotation.UseCase
import org.springframework.cache.annotation.Cacheable
import org.springframework.transaction.annotation.Transactional

@UseCase
class GetCategoryTreeUseCase(private val categoryRepository: CategoryRepository) {
    @Cacheable(cacheNames = ["categoryTree"])
    @Transactional(readOnly = true)
    fun execute(): List<CategoryTreeInfo> {
        val allCategories = categoryRepository.findAllByOrderByDepthAscOrderingAsc()

        return build(allCategories)
    }

    private fun build(categories: List<Category>): List<CategoryTreeInfo> {
        val nodeMap = categories
            .map { c -> CategoryTreeNode(c.id!!, c.name, c.depth, c.parent?.id) }
            .associateBy { it.id }

        nodeMap.values.forEach { node ->
            node.parentId?.let { parentId ->
                nodeMap[parentId]?.children?.add(node)
            }
        }

        val roots = nodeMap.values.filter { it.parentId == null }

        fun toInfo(node: CategoryTreeNode): CategoryTreeInfo = CategoryTreeInfo(
            id = node.id,
            name = node.name,
            depth = node.depth,
            children = node.children.map { toInfo(it) },
        )

        return roots.map { toInfo(it) }
    }
}
