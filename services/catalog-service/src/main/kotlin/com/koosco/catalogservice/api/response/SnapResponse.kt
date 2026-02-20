package com.koosco.catalogservice.api.response

import com.koosco.catalogservice.application.result.SnapResult
import com.koosco.catalogservice.domain.enums.ContentStatus
import java.time.LocalDateTime

data class SnapResponse(
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
        fun from(result: SnapResult): SnapResponse = SnapResponse(
            snapId = result.snapId,
            productId = result.productId,
            userId = result.userId,
            caption = result.caption,
            status = result.status,
            likeCount = result.likeCount,
            imageUrls = result.imageUrls,
            createdAt = result.createdAt,
            updatedAt = result.updatedAt,
        )
    }
}
