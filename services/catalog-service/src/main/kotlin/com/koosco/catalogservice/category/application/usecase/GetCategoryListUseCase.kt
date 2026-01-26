package com.koosco.catalogservice.category.application.usecase

import com.koosco.catalogservice.category.application.dto.CategoryInfo
import com.koosco.catalogservice.category.application.dto.GetCategoryListCommand
import com.koosco.catalogservice.category.application.port.CategoryRepository
import com.koosco.common.core.annotation.UseCase
import org.springframework.transaction.annotation.Transactional

@UseCase
class GetCategoryListUseCase(private val categoryRepository: CategoryRepository) {

    @Transactional(readOnly = true)
    fun execute(command: GetCategoryListCommand): List<CategoryInfo> {
        val categories = if (command.parentId != null) {
            categoryRepository.findByParentIdOrderByOrderingAsc(command.parentId)
        } else {
            categoryRepository.findByParentIsNull()
        }

        return categories.map { CategoryInfo.from(it) }
    }
}
