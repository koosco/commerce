package com.koosco.catalogservice.application.usecase

import com.koosco.catalogservice.application.command.CreateCategoryCommand
import com.koosco.catalogservice.application.dto.CategoryInfo
import com.koosco.catalogservice.application.port.CatalogIdempotencyRepository
import com.koosco.catalogservice.application.port.CategoryRepository
import com.koosco.catalogservice.common.error.CatalogErrorCode
import com.koosco.catalogservice.domain.entity.CatalogIdempotency
import com.koosco.catalogservice.domain.entity.Category
import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.exception.ConflictException
import com.koosco.common.core.exception.NotFoundException
import org.springframework.cache.annotation.CacheEvict
import org.springframework.transaction.annotation.Transactional

@UseCase
class CreateCategoryUseCase(
    private val categoryRepository: CategoryRepository,
    private val catalogIdempotencyRepository: CatalogIdempotencyRepository,
) {

    @CacheEvict(cacheNames = ["categoryTree"], allEntries = true)
    @Transactional
    fun execute(command: CreateCategoryCommand, idempotencyKey: String? = null): CategoryInfo {
        if (idempotencyKey != null) {
            val existing = catalogIdempotencyRepository.findByIdempotencyKeyAndResourceType(
                idempotencyKey,
                "CATEGORY",
            )
            if (existing != null) {
                val category = categoryRepository.findByIdOrNull(existing.resourceId)
                    ?: throw NotFoundException(CatalogErrorCode.CATEGORY_NOT_FOUND)
                return CategoryInfo.from(category)
            }
        }

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

        if (idempotencyKey != null) {
            catalogIdempotencyRepository.save(
                CatalogIdempotency.create(idempotencyKey, "CATEGORY", savedCategory.id!!),
            )
        }

        return CategoryInfo.from(savedCategory)
    }
}
