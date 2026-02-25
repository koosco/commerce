package com.koosco.catalogservice.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.PreUpdate
import jakarta.persistence.Table
import org.hibernate.annotations.SQLRestriction
import java.time.LocalDateTime

@Entity
@Table(name = "brands")
@SQLRestriction("deleted_at IS NULL")
class Brand(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, length = 100)
    var name: String,

    @Column(name = "logo_image_url", length = 500)
    var logoImageUrl: String? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "deleted_at")
    var deletedAt: LocalDateTime? = null,
) {
    @PreUpdate
    fun preUpdate() {
        updatedAt = LocalDateTime.now()
    }

    fun softDelete() {
        deletedAt = LocalDateTime.now()
    }

    fun update(name: String?, logoImageUrl: String?) {
        name?.let { this.name = it }
        logoImageUrl?.let { this.logoImageUrl = it }
    }
}
