package com.koosco.userservice.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.time.Instant

@Entity
@Table(
    name = "user_idempotency",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uq_user_idempotency",
            columnNames = ["idempotency_key", "resource_type"],
        ),
    ],
    indexes = [
        Index(name = "idx_user_idempotency_resource", columnList = "resource_type, resource_id"),
    ],
)
class UserIdempotency(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "idempotency_key", nullable = false, length = 100)
    val idempotencyKey: String,

    @Column(name = "resource_type", nullable = false, length = 30)
    val resourceType: String,

    @Column(name = "resource_id", nullable = false)
    val resourceId: Long,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),
) {
    companion object {
        const val USER = "USER"
        const val ADDRESS = "ADDRESS"

        fun create(idempotencyKey: String, resourceType: String, resourceId: Long): UserIdempotency = UserIdempotency(
            idempotencyKey = idempotencyKey,
            resourceType = resourceType,
            resourceId = resourceId,
        )
    }
}
