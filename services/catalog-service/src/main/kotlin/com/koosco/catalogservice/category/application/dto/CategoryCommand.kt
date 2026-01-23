package com.koosco.catalogservice.category.application.dto

data class GetCategoryListCommand(val parentId: Long?)

data class CreateCategoryCommand(val name: String, val parentId: Long?, val ordering: Int = 0)

data class CreateCategoryTreeCommand(
    val name: String,
    val ordering: Int = 0,
    val children: List<CreateCategoryTreeCommand> = emptyList(),
)
