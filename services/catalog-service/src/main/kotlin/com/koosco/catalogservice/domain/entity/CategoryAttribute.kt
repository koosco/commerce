package com.koosco.catalogservice.domain.entity

import com.koosco.catalogservice.domain.enums.AttributeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.PreUpdate
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "category_attributes")
class CategoryAttribute(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "category_id", nullable = false)
    val categoryId: Long,

    @Column(nullable = false, length = 100)
    var name: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    val type: AttributeType,

    @Column(nullable = false)
    var required: Boolean = false,

    @Column(length = 1000)
    var options: String? = null,

    @Column(nullable = false)
    var ordering: Int = 0,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),
) {
    @PreUpdate
    fun preUpdate() {
        updatedAt = LocalDateTime.now()
    }

    fun update(name: String?, required: Boolean?, options: String?, ordering: Int?) {
        name?.let { this.name = it }
        required?.let { this.required = it }
        options?.let { this.options = it }
        ordering?.let { this.ordering = it }
    }

    fun getOptionList(): List<String> =
        options?.split(",")?.map { it.trim() }?.filter { it.isNotBlank() } ?: emptyList()

    fun validateValue(value: String) {
        when (type) {
            AttributeType.NUMBER -> {
                require(value.toDoubleOrNull() != null) {
                    "속성 '$name'의 값은 숫자여야 합니다."
                }
            }
            AttributeType.BOOLEAN -> {
                require(value.lowercase() in listOf("true", "false")) {
                    "속성 '$name'의 값은 true 또는 false여야 합니다."
                }
            }
            AttributeType.ENUM -> {
                val allowedOptions = getOptionList()
                require(allowedOptions.isEmpty() || value in allowedOptions) {
                    "속성 '$name'의 값은 ${allowedOptions.joinToString(", ")} 중 하나여야 합니다."
                }
            }
            AttributeType.STRING -> {
                // No validation needed for STRING type
            }
        }
    }

    companion object {
        fun create(
            categoryId: Long,
            name: String,
            type: AttributeType,
            required: Boolean,
            options: String?,
            ordering: Int,
        ): CategoryAttribute {
            if (type == AttributeType.ENUM) {
                requireNotNull(options) { "ENUM 타입의 속성은 options가 필수입니다." }
                require(options.isNotBlank()) { "ENUM 타입의 속성은 options가 비어있을 수 없습니다." }
            }

            return CategoryAttribute(
                categoryId = categoryId,
                name = name,
                type = type,
                required = required,
                options = options,
                ordering = ordering,
            )
        }
    }
}
