package com.koosco.catalogservice.category.application.usecase

import com.koosco.catalogservice.category.application.dto.CategoryTreeInfo
import com.koosco.catalogservice.category.application.dto.CreateCategoryTreeCommand
import com.koosco.catalogservice.category.application.port.CategoryRepository
import com.koosco.catalogservice.category.domain.Category
import com.koosco.common.core.annotation.UseCase
import org.springframework.transaction.annotation.Transactional

@UseCase
class CreateCategoryTreeUseCase(private val categoryRepository: CategoryRepository) {

    @Transactional
    fun execute(command: CreateCategoryTreeCommand): CategoryTreeInfo {
        val rootCategory = Category.createTree(command)

        categoryRepository.save(rootCategory)

        return CategoryTreeInfo.from(rootCategory)
    }
}
