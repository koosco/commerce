package com.koosco.catalogservice.category.application.usecase

import com.koosco.catalogservice.category.application.dto.CategoryInfo
import com.koosco.catalogservice.category.application.dto.CreateCategoryCommand
import com.koosco.catalogservice.category.application.repository.CategoryRepository
import com.koosco.catalogservice.category.domain.Category
import com.koosco.catalogservice.common.exception.CatalogErrorCode
import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.exception.ConflictException
import com.koosco.common.core.exception.NotFoundException
import org.springframework.transaction.annotation.Transactional

@UseCase
class CreateCategoryUseCase(private val categoryRepository: CategoryRepository) {

    @Transactional
    fun execute(command: CreateCategoryCommand): CategoryInfo {
        val parent = if (command.parentId != null) {
            categoryRepository.findByIdOrNull(command.parentId)
                ?: throw NotFoundException(CatalogErrorCode.CATEGORY_NOT_FOUND)
        } else {
            null
        }

        // 중복 카테고리 체크: 같은 부모 아래 같은 이름의 카테고리가 있는지 확인
        if (categoryRepository.existsByNameAndParent(command.name, parent)) {
            throw ConflictException(CatalogErrorCode.CATEGORY_NAME_CONFLICT)
        }

        val category = Category.of(
            name = command.name,
            parent = parent,
            ordering = command.ordering,
        )
        val savedCategory = categoryRepository.save(category)

        return CategoryInfo.from(savedCategory)
    }
}
