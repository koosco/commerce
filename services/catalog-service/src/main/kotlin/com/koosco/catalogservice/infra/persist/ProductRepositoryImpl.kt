package com.koosco.catalogservice.infra.persist

import com.koosco.catalogservice.application.command.GetProductListCommand
import com.koosco.catalogservice.application.port.ProductRepository
import com.koosco.catalogservice.domain.entity.Product
import org.springframework.data.domain.Page
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class ProductRepositoryImpl(
    private val jpaProductRepository: JpaProductRepository,
    private val productQuery: ProductQuery,
) : ProductRepository {

    override fun save(product: Product): Product = jpaProductRepository.save(product)

    override fun findOrNull(productId: Long): Product? = jpaProductRepository.findByIdOrNull(productId)

    override fun findByIdWithOptions(productId: Long): Product? = jpaProductRepository.findByIdWithOptions(productId)

    override fun findBySkuId(skuId: String): Product? = jpaProductRepository.findBySkuId(skuId)

    override fun search(command: GetProductListCommand): Page<Product> = productQuery.search(command)
}
