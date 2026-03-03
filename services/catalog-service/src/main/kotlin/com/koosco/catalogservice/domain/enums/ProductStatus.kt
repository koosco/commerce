package com.koosco.catalogservice.domain.enums

enum class ProductStatus {
    DRAFT,
    ACTIVE,
    SUSPENDED,
    DELETED,
    ;

    fun canTransitionTo(target: ProductStatus): Boolean = when (this) {
        DRAFT -> target == ACTIVE
        ACTIVE -> target in setOf(SUSPENDED, DELETED)
        SUSPENDED -> target in setOf(ACTIVE, DELETED)
        DELETED -> false
    }
}
