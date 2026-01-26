package com.koosco.catalogservice.category.application.usecase

import com.koosco.catalogservice.category.application.converter.CategoryTreeBuilder
import com.koosco.catalogservice.category.application.dto.CategoryTreeInfo
import com.koosco.catalogservice.category.application.port.CategoryRepository
import com.koosco.common.core.annotation.UseCase
import org.springframework.transaction.annotation.Transactional

@UseCase
class GetCategoryTreeUseCase(
    private val categoryRepository: CategoryRepository,
    private val categoryTreeBuilder: CategoryTreeBuilder,
) {
    @Transactional(readOnly = true)
    fun execute(): List<CategoryTreeInfo> {
        val allCategories = categoryRepository.findAllByOrderByDepthAscOrderingAsc()

        return categoryTreeBuilder.build(allCategories)
    }
}
