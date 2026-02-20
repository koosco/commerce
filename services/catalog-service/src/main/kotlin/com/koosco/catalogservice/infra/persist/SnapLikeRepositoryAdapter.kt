package com.koosco.catalogservice.infra.persist

import com.koosco.catalogservice.application.port.SnapLikeRepository
import com.koosco.catalogservice.domain.entity.SnapLike
import com.koosco.catalogservice.domain.entity.SnapLikeId
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class SnapLikeRepositoryAdapter(private val jpaSnapLikeRepository: JpaSnapLikeRepository) : SnapLikeRepository {

    override fun findById(id: SnapLikeId): SnapLike? = jpaSnapLikeRepository.findByIdOrNull(id)

    override fun save(like: SnapLike): SnapLike = jpaSnapLikeRepository.save(like)

    override fun delete(like: SnapLike) = jpaSnapLikeRepository.delete(like)
}
