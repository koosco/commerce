package com.koosco.catalogservice.infra.persist

import com.koosco.catalogservice.domain.entity.Snap
import com.koosco.catalogservice.domain.enums.ContentStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface JpaSnapRepository : JpaRepository<Snap, Long> {

    fun findByStatusNot(status: ContentStatus, pageable: Pageable): Page<Snap>
}
