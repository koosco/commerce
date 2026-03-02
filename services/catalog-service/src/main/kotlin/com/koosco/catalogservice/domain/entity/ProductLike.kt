package com.koosco.catalogservice.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "product_like")
@IdClass(ProductLikeId::class)
class ProductLike(

    @Id
    @Column(name = "product_id")
    val productId: Long,

    @Id
    @Column(name = "user_id")
    val userId: Long,

    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
)
