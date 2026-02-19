package com.koosco.catalogservice.infra.persist

import com.koosco.catalogservice.application.port.ProductRepository
import com.koosco.catalogservice.domain.entity.Product
import com.koosco.catalogservice.domain.enums.ProductStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class ProductRepositoryImpl(private val jpaProductRepository: JpaProductRepository) : ProductRepository {

    override fun save(product: Product): Product = jpaProductRepository.save(product)

    override fun findOrNull(productId: Long): Product? = jpaProductRepository.findByIdOrNull(productId)

    override fun findByIdWithOptions(productId: Long): Product? = jpaProductRepository.findByIdWithOptions(productId)

    override fun findByConditions(
        categoryId: Long?,
        keyword: String?,
        status: ProductStatus,
        pageable: Pageable,
    ): Page<Product> = jpaProductRepository.findByConditions(categoryId, keyword, status, pageable)
}
