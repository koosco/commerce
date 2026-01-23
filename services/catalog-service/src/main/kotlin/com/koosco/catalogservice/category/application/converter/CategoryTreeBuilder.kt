package com.koosco.catalogservice.category.application.converter

import com.koosco.catalogservice.category.application.dto.CategoryTreeInfo
import com.koosco.catalogservice.category.domain.Category
import org.springframework.stereotype.Component
import kotlin.collections.map

@Component
class CategoryTreeBuilder {

    fun build(categories: List<Category>): List<CategoryTreeInfo> {
        data class TreeNode(
            val id: Long,
            val name: String,
            val depth: Int,
            val parentId: Long?,
            val children: MutableList<TreeNode> = mutableListOf(),
        )

        val nodeMap = categories
            .map { c -> TreeNode(c.id!!, c.name, c.depth, c.parent?.id) }
            .associateBy { it.id }

        nodeMap.values.forEach { node ->
            node.parentId?.let { parentId ->
                nodeMap[parentId]?.children?.add(node)
            }
        }

        val roots = nodeMap.values.filter { it.parentId == null }

        fun toInfo(node: TreeNode): CategoryTreeInfo = CategoryTreeInfo(
            id = node.id,
            name = node.name,
            depth = node.depth,
            children = node.children.map { toInfo(it) },
        )

        return roots.map { toInfo(it) }
    }
}
