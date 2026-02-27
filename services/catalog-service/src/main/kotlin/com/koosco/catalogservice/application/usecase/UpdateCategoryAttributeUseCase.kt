package com.koosco.catalogservice.application.usecase

import com.koosco.catalogservice.application.command.UpdateCategoryAttributeCommand
import com.koosco.catalogservice.application.port.CategoryAttributeRepository
import com.koosco.catalogservice.application.result.CategoryAttributeInfo
import com.koosco.catalogservice.common.error.CatalogErrorCode
import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.exception.NotFoundException
import org.springframework.transaction.annotation.Transactional

@UseCase
class UpdateCategoryAttributeUseCase(private val categoryAttributeRepository: CategoryAttributeRepository) {

    @Transactional
    fun execute(command: UpdateCategoryAttributeCommand): CategoryAttributeInfo {
        val attribute = categoryAttributeRepository.findOrNull(command.attributeId)
            ?: throw NotFoundException(CatalogErrorCode.ATTRIBUTE_NOT_FOUND)

        attribute.update(
            name = command.name,
            required = command.required,
            options = command.options,
            ordering = command.ordering,
        )

        return CategoryAttributeInfo.from(attribute)
    }
}
