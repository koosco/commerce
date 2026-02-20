package com.koosco.catalogservice.application.port

import com.koosco.catalogservice.domain.entity.ReviewLike
import com.koosco.catalogservice.domain.entity.ReviewLikeId

interface ReviewLikeRepository {

    fun findById(id: ReviewLikeId): ReviewLike?

    fun save(like: ReviewLike): ReviewLike

    fun delete(like: ReviewLike)
}
