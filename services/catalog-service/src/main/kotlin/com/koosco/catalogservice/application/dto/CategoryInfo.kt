package com.koosco.catalogservice.application.dto

import com.koosco.catalogservice.domain.entity.Category

data class CategoryInfo(val id: Long, val name: String, val parentId: Long?, val depth: Int, val ordering: Int) {
    companion object {
        fun from(category: Category): CategoryInfo = CategoryInfo(
            id = category.id!!,
            name = category.name,
            parentId = category.parent?.id,
            depth = category.depth,
            ordering = category.ordering,
        )
    }
}

data class CategoryTreeInfo(val id: Long, val name: String, val depth: Int, val children: List<CategoryTreeInfo>) {
    companion object {
        fun from(category: Category): CategoryTreeInfo = CategoryTreeInfo(
            id = category.id!!,
            name = category.name,
            depth = category.depth,
            children = category.children.map { from(it) },
        )
    }
}
