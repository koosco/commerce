package com.koosco.catalogservice.application.port

import com.koosco.catalogservice.domain.entity.Snap
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface SnapRepository {

    fun save(snap: Snap): Snap

    fun findByIdOrNull(snapId: Long): Snap?

    fun findAll(pageable: Pageable): Page<Snap>
}
