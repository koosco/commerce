package com.koosco.catalogservice.infra.persist

import com.koosco.catalogservice.application.port.ProductLikeRepository
import com.koosco.catalogservice.domain.entity.ProductLike
import com.koosco.catalogservice.domain.entity.ProductLikeId
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class ProductLikeRepositoryAdapter(private val jpaProductLikeRepository: JpaProductLikeRepository) :
    ProductLikeRepository {

    override fun findById(id: ProductLikeId): ProductLike? = jpaProductLikeRepository.findByIdOrNull(id)

    override fun save(like: ProductLike): ProductLike = jpaProductLikeRepository.save(like)

    override fun delete(like: ProductLike) = jpaProductLikeRepository.delete(like)
}
