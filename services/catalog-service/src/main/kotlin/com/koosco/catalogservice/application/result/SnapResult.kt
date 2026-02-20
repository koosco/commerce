package com.koosco.catalogservice.application.result

import com.koosco.catalogservice.domain.entity.Snap
import com.koosco.catalogservice.domain.enums.ContentStatus
import java.time.LocalDateTime

data class SnapResult(
    val snapId: Long,
    val productId: Long,
    val userId: Long,
    val caption: String?,
    val status: ContentStatus,
    val likeCount: Int,
    val imageUrls: List<String>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    companion object {
        fun from(snap: Snap): SnapResult = SnapResult(
            snapId = snap.id!!,
            productId = snap.productId,
            userId = snap.userId,
            caption = snap.caption,
            status = snap.status,
            likeCount = snap.likeCount,
            imageUrls = snap.images.sortedBy { it.ordering }.map { it.imageUrl },
            createdAt = snap.createdAt,
            updatedAt = snap.updatedAt,
        )
    }
}
