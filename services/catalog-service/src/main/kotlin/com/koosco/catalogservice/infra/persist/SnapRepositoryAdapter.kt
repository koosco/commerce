package com.koosco.catalogservice.infra.persist

import com.koosco.catalogservice.application.port.SnapRepository
import com.koosco.catalogservice.domain.entity.Snap
import com.koosco.catalogservice.domain.enums.ContentStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class SnapRepositoryAdapter(private val jpaSnapRepository: JpaSnapRepository) : SnapRepository {

    override fun save(snap: Snap): Snap = jpaSnapRepository.save(snap)

    override fun findByIdOrNull(snapId: Long): Snap? = jpaSnapRepository.findByIdOrNull(snapId)

    override fun findAll(pageable: Pageable): Page<Snap> =
        jpaSnapRepository.findByStatusNot(ContentStatus.DELETED, pageable)
}
