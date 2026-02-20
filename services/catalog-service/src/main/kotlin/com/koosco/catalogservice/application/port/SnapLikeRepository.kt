package com.koosco.catalogservice.application.port

import com.koosco.catalogservice.domain.entity.SnapLike
import com.koosco.catalogservice.domain.entity.SnapLikeId

interface SnapLikeRepository {

    fun findById(id: SnapLikeId): SnapLike?

    fun save(like: SnapLike): SnapLike

    fun delete(like: SnapLike)
}
