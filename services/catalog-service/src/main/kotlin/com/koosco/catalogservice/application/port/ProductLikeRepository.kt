package com.koosco.catalogservice.application.port

import com.koosco.catalogservice.domain.entity.ProductLike
import com.koosco.catalogservice.domain.entity.ProductLikeId

interface ProductLikeRepository {

    fun findById(id: ProductLikeId): ProductLike?

    fun save(like: ProductLike): ProductLike

    fun delete(like: ProductLike)
}
