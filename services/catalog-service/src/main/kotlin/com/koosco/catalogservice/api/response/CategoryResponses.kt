package com.koosco.catalogservice.api.response

import com.koosco.catalogservice.application.dto.CategoryInfo
import com.koosco.catalogservice.application.dto.CategoryTreeInfo

data class CategoryResponse(val id: Long, val name: String, val parentId: Long?, val depth: Int, val ordering: Int) {
    companion object {
        fun from(categoryInfo: CategoryInfo): CategoryResponse = CategoryResponse(
            id = categoryInfo.id,
            name = categoryInfo.name,
            parentId = categoryInfo.parentId,
            depth = categoryInfo.depth,
            ordering = categoryInfo.ordering,
        )
    }
}

data class CategoryTreeResponse(
    val id: Long,
    val name: String,
    val depth: Int,
    val children: List<CategoryTreeResponse>,
) {
    companion object {
        fun from(treeInfo: CategoryTreeInfo): CategoryTreeResponse = CategoryTreeResponse(
            id = treeInfo.id,
            name = treeInfo.name,
            depth = treeInfo.depth,
            children = treeInfo.children.map { from(it) },
        )
    }
}
