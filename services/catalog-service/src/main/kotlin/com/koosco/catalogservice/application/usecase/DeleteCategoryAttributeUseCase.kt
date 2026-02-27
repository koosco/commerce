package com.koosco.catalogservice.application.usecase

import com.koosco.catalogservice.application.command.DeleteCategoryAttributeCommand
import com.koosco.catalogservice.application.port.CategoryAttributeRepository
import com.koosco.catalogservice.common.error.CatalogErrorCode
import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.exception.NotFoundException
import org.springframework.transaction.annotation.Transactional

@UseCase
class DeleteCategoryAttributeUseCase(private val categoryAttributeRepository: CategoryAttributeRepository) {

    @Transactional
    fun execute(command: DeleteCategoryAttributeCommand) {
        val attribute = categoryAttributeRepository.findOrNull(command.attributeId)
            ?: throw NotFoundException(CatalogErrorCode.ATTRIBUTE_NOT_FOUND)

        categoryAttributeRepository.delete(attribute)
    }
}
