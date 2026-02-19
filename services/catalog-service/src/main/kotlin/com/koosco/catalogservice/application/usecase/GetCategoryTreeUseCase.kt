package com.koosco.catalogservice.application.usecase

import com.koosco.catalogservice.application.converter.CategoryTreeBuilder
import com.koosco.catalogservice.application.dto.CategoryTreeInfo
import com.koosco.catalogservice.application.port.CategoryRepository
import com.koosco.common.core.annotation.UseCase
import org.springframework.cache.annotation.Cacheable
import org.springframework.transaction.annotation.Transactional

@UseCase
class GetCategoryTreeUseCase(
    private val categoryRepository: CategoryRepository,
    private val categoryTreeBuilder: CategoryTreeBuilder,
) {
    @Cacheable(cacheNames = ["categoryTree"])
    @Transactional(readOnly = true)
    fun execute(): List<CategoryTreeInfo> {
        val allCategories = categoryRepository.findAllByOrderByDepthAscOrderingAsc()

        return categoryTreeBuilder.build(allCategories)
    }
}
