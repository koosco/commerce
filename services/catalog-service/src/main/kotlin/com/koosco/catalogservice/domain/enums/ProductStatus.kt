package com.koosco.catalogservice.domain.enums

enum class ProductStatus {
    DRAFT,
    ACTIVE,
    SUSPENDED,
    OUT_OF_STOCK,
    DELETED,
    ;

    fun canTransitionTo(target: ProductStatus): Boolean = when (this) {
        DRAFT -> target == ACTIVE
        ACTIVE -> target in setOf(SUSPENDED, OUT_OF_STOCK, DELETED)
        SUSPENDED -> target in setOf(ACTIVE, DELETED)
        OUT_OF_STOCK -> target in setOf(ACTIVE, DELETED)
        DELETED -> false
    }
}
