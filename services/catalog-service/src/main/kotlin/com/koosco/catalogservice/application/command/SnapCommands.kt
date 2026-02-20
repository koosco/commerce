package com.koosco.catalogservice.application.command

data class CreateSnapCommand(
    val productId: Long,
    val userId: Long,
    val caption: String?,
    val imageUrls: List<String> = emptyList(),
)

data class UpdateSnapCommand(val snapId: Long, val userId: Long, val caption: String?)

data class DeleteSnapCommand(val snapId: Long, val userId: Long)
