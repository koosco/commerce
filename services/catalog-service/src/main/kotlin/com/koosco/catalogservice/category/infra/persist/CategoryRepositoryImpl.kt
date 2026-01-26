package com.koosco.catalogservice.category.infra.persist

import com.koosco.catalogservice.category.application.port.CategoryRepository
import com.koosco.catalogservice.category.domain.Category
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class CategoryRepositoryImpl(private val jpaCategoryRepository: JpaCategoryRepository) : CategoryRepository {

    override fun save(category: Category): Category = jpaCategoryRepository.save(category)

    override fun findByIdOrNull(id: Long): Category? = jpaCategoryRepository.findByIdOrNull(id)

    override fun findByParentIdOrderByOrderingAsc(parentId: Long?): List<Category> =
        jpaCategoryRepository.findByParentIdOrderByOrderingAsc(parentId)

    override fun findByParentIsNull(): List<Category> = jpaCategoryRepository.findByParentIsNull()

    override fun findByDepthOrderByOrderingAsc(depth: Int): List<Category> =
        jpaCategoryRepository.findByDepthOrderByOrderingAsc(depth)

    override fun findAllByOrderByDepthAscOrderingAsc(): List<Category> =
        jpaCategoryRepository.findAllByOrderByDepthAscOrderingAsc()

    override fun existsByNameAndParent(name: String, parent: Category?): Boolean =
        jpaCategoryRepository.existsByNameAndParent(name, parent)
}
