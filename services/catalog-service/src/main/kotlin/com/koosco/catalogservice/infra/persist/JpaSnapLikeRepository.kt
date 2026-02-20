package com.koosco.catalogservice.infra.persist

import com.koosco.catalogservice.domain.entity.SnapLike
import com.koosco.catalogservice.domain.entity.SnapLikeId
import org.springframework.data.jpa.repository.JpaRepository

interface JpaSnapLikeRepository : JpaRepository<SnapLike, SnapLikeId>
