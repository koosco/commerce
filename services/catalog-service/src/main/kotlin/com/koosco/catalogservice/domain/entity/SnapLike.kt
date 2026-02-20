package com.koosco.catalogservice.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "content_snap_like")
@IdClass(SnapLikeId::class)
class SnapLike(

    @Id
    @Column(name = "snap_id")
    val snapId: Long,

    @Id
    @Column(name = "user_id")
    val userId: Long,

    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
)
