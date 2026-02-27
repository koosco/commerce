package com.koosco.catalogservice.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.PreUpdate
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "product_attribute_values")
class ProductAttributeValue(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "product_id", nullable = false)
    val productId: Long,

    @Column(name = "attribute_id", nullable = false)
    val attributeId: Long,

    @Column(nullable = false, length = 500)
    var value: String,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),
) {
    @PreUpdate
    fun preUpdate() {
        updatedAt = LocalDateTime.now()
    }

    fun updateValue(newValue: String) {
        this.value = newValue
    }

    companion object {
        fun create(productId: Long, attributeId: Long, value: String): ProductAttributeValue = ProductAttributeValue(
            productId = productId,
            attributeId = attributeId,
            value = value,
        )
    }
}
