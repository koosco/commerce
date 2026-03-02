package com.koosco.catalogservice.domain.vo

data class CategoryTreeSpec(
    val name: String,
    val ordering: Int = 0,
    val children: List<CategoryTreeSpec> = emptyList(),
)
