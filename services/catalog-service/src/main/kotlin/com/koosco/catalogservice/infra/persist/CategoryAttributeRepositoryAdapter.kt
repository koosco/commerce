package com.koosco.catalogservice.infra.persist

import com.koosco.catalogservice.application.port.CategoryAttributeRepository
import com.koosco.catalogservice.domain.entity.CategoryAttribute
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class CategoryAttributeRepositoryAdapter(private val jpaCategoryAttributeRepository: JpaCategoryAttributeRepository) :
    CategoryAttributeRepository {

    override fun save(attribute: CategoryAttribute): CategoryAttribute = jpaCategoryAttributeRepository.save(attribute)

    override fun findOrNull(id: Long): CategoryAttribute? = jpaCategoryAttributeRepository.findByIdOrNull(id)

    override fun findByCategoryId(categoryId: Long): List<CategoryAttribute> =
        jpaCategoryAttributeRepository.findByCategoryIdOrderByOrderingAsc(categoryId)

    override fun findByCategoryIdIn(categoryIds: List<Long>): List<CategoryAttribute> =
        jpaCategoryAttributeRepository.findByCategoryIdInOrderByOrderingAsc(categoryIds)

    override fun delete(attribute: CategoryAttribute) = jpaCategoryAttributeRepository.delete(attribute)
}
