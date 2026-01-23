package com.koosco.catalogservice.product.application.port

import com.koosco.catalogservice.product.domain.entity.Product
import com.koosco.catalogservice.product.domain.enums.ProductStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ProductRepository {
    fun save(product: Product): Product

    fun findOrNull(productId: Long): Product?

    fun findByIdWithOptions(productId: Long): Product?

    fun findByConditions(categoryId: Long?, keyword: String?, status: ProductStatus, pageable: Pageable): Page<Product>
}
