package com.koosco.catalogservice.application.usecase

import com.koosco.catalogservice.application.command.CreateCategoryAttributeCommand
import com.koosco.catalogservice.application.port.CategoryAttributeRepository
import com.koosco.catalogservice.application.port.CategoryRepository
import com.koosco.catalogservice.application.result.CategoryAttributeInfo
import com.koosco.catalogservice.common.error.CatalogErrorCode
import com.koosco.catalogservice.domain.entity.CategoryAttribute
import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.exception.NotFoundException
import org.springframework.transaction.annotation.Transactional

@UseCase
class CreateCategoryAttributeUseCase(
    private val categoryRepository: CategoryRepository,
    private val categoryAttributeRepository: CategoryAttributeRepository,
) {

    @Transactional
    fun execute(command: CreateCategoryAttributeCommand): CategoryAttributeInfo {
        categoryRepository.findByIdOrNull(command.categoryId)
            ?: throw NotFoundException(CatalogErrorCode.CATEGORY_NOT_FOUND)

        val attribute = CategoryAttribute.create(
            categoryId = command.categoryId,
            name = command.name,
            type = command.type,
            required = command.required,
            options = command.options,
            ordering = command.ordering,
        )

        val saved = categoryAttributeRepository.save(attribute)

        return CategoryAttributeInfo.from(saved)
    }
}
