package com.koosco.catalogservice.application.usecase

import com.koosco.catalogservice.application.dto.CategoryInfo
import com.koosco.catalogservice.application.port.CategoryRepository
import com.koosco.catalogservice.common.error.CatalogErrorCode
import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.exception.NotFoundException
import org.springframework.transaction.annotation.Transactional

@UseCase
class GetCategoryByIdUseCase(private val categoryRepository: CategoryRepository) {

    @Transactional(readOnly = true)
    fun execute(categoryId: Long): CategoryInfo {
        val category = categoryRepository.findByIdOrNull(categoryId)
            ?: throw NotFoundException(CatalogErrorCode.CATEGORY_NOT_FOUND)
        return CategoryInfo.from(category)
    }
}
