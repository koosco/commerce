package com.koosco.catalogservice.application.port

import com.koosco.catalogservice.application.command.GetProductListCommand
import com.koosco.catalogservice.domain.entity.Product
import org.springframework.data.domain.Page

interface ProductRepository {
    fun save(product: Product): Product

    fun findOrNull(productId: Long): Product?

    fun findByIdWithOptions(productId: Long): Product?

    fun findBySkuId(skuId: String): Product?

    fun search(command: GetProductListCommand): Page<Product>
}
