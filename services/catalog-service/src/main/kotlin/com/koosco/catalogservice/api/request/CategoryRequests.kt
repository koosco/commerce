package com.koosco.catalogservice.api.request

import com.koosco.catalogservice.application.command.CreateCategoryCommand
import com.koosco.catalogservice.application.command.CreateCategoryTreeCommand
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

data class CategoryCreateRequest(
    @field:NotBlank(message = "카테고리 이름은 필수입니다.")
    val name: String,
    val parentId: Long?,
    @field:Min(value = 0, message = "카테고리 순서는 0 이상이어야 합니다.")
    val ordering: Int = 0,
) {
    fun toCommand(): CreateCategoryCommand = CreateCategoryCommand(
        name = name,
        parentId = parentId,
        ordering = ordering,
    )
}

data class CategoryTreeCreateRequest(
    @field:NotBlank(message = "카테고리 이름은 필수입니다.")
    val name: String,
    @field:Min(value = 0, message = "카테고리 순서는 0 이상이어야 합니다.")
    val ordering: Int = 0,
    @field:Valid
    val children: List<CategoryTreeCreateRequest> = emptyList(),
) {
    fun toCommand(): CreateCategoryTreeCommand = CreateCategoryTreeCommand(
        name = name,
        ordering = ordering,
        children = children.map { it.toCommand() },
    )
}
