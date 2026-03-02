package com.koosco.searchservice.infra.persist

import com.koosco.searchservice.application.port.SearchProductRepository
import com.koosco.searchservice.domain.entity.SearchProduct
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class SearchProductRepositoryAdapter(private val jpaSearchProductRepository: JpaSearchProductRepository) :
    SearchProductRepository {

    override fun save(searchProduct: SearchProduct): SearchProduct = jpaSearchProductRepository.save(searchProduct)

    override fun findByProductId(productId: Long): SearchProduct? =
        jpaSearchProductRepository.findByProductId(productId)

    override fun findOrNull(id: Long): SearchProduct? = jpaSearchProductRepository.findByIdOrNull(id)

    override fun deleteByProductId(productId: Long) = jpaSearchProductRepository.deleteByProductId(productId)
}
