package com.koosco.catalogservice.application.usecase

import com.koosco.catalogservice.application.command.GetCategoryAttributesCommand
import com.koosco.catalogservice.application.port.CategoryAttributeRepository
import com.koosco.catalogservice.application.port.CategoryRepository
import com.koosco.catalogservice.application.result.CategoryAttributeInfo
import com.koosco.catalogservice.common.error.CatalogErrorCode
import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.exception.NotFoundException
import org.springframework.transaction.annotation.Transactional

@UseCase
class GetCategoryAttributesUseCase(
    private val categoryRepository: CategoryRepository,
    private val categoryAttributeRepository: CategoryAttributeRepository,
) {

    @Transactional(readOnly = true)
    fun execute(command: GetCategoryAttributesCommand): List<CategoryAttributeInfo> {
        val category = categoryRepository.findByIdOrNull(command.categoryId)
            ?: throw NotFoundException(CatalogErrorCode.CATEGORY_NOT_FOUND)

        if (!command.includeInherited) {
            return categoryAttributeRepository.findByCategoryId(command.categoryId)
                .map { CategoryAttributeInfo.from(it) }
        }

        // Collect category IDs from current to root for attribute inheritance
        val categoryIds = mutableListOf<Long>()
        var current = category
        while (true) {
            categoryIds.add(current.id!!)
            current = current.parent ?: break
        }

        val allAttributes = categoryAttributeRepository.findByCategoryIdIn(categoryIds)

        return allAttributes.map { attr ->
            CategoryAttributeInfo.from(attr, inherited = attr.categoryId != command.categoryId)
        }.sortedBy { it.ordering }
    }
}
