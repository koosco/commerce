package com.koosco.catalogservice.application.usecase

import com.koosco.catalogservice.application.command.GetCategoryListCommand
import com.koosco.catalogservice.application.dto.CategoryInfo
import com.koosco.catalogservice.application.port.CategoryRepository
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
