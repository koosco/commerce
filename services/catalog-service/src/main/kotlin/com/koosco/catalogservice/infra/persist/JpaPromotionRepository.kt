package com.koosco.catalogservice.infra.persist

import com.koosco.catalogservice.domain.entity.Promotion
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface JpaPromotionRepository : JpaRepository<Promotion, Long> {

    @Query(
        """
        SELECT p FROM Promotion p
        WHERE p.productId = :productId
          AND p.startAt <= :now
          AND p.endAt >= :now
        ORDER BY p.priority ASC
        """,
    )
    fun findActiveByProductId(@Param("productId") productId: Long, @Param("now") now: LocalDateTime): List<Promotion>

    @Query(
        """
        SELECT p FROM Promotion p
        WHERE p.productId IN :productIds
          AND p.startAt <= :now
          AND p.endAt >= :now
        ORDER BY p.priority ASC
        """,
    )
    fun findActiveByProductIds(
        @Param("productIds") productIds: List<Long>,
        @Param("now") now: LocalDateTime,
    ): List<Promotion>
}
