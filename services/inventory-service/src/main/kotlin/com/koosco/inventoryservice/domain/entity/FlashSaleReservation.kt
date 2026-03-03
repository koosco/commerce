package com.koosco.inventoryservice.domain.entity

import com.koosco.inventoryservice.domain.enums.ReservationStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.time.LocalDateTime

/**
 * Flash Sale 예약 엔티티
 *
 * Flash Sale에서 사용자가 재고를 선점(예약)하면 생성되며,
 * 이후 주문 확정/취소/만료에 따라 상태가 전이됩니다.
 */
@Entity
@Table(
    name = "flash_sale_reservation",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uq_flash_sale_reservation_id",
            columnNames = ["reservation_id"],
        ),
    ],
    indexes = [
        Index(name = "idx_fsr_flash_sale_id", columnList = "flash_sale_id"),
        Index(name = "idx_fsr_user_id", columnList = "user_id"),
        Index(name = "idx_fsr_status", columnList = "status"),
        Index(name = "idx_fsr_expires_at", columnList = "expires_at"),
    ],
)
class FlashSaleReservation(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "reservation_id", nullable = false, length = 64)
    val reservationId: String,

    @Column(name = "flash_sale_id", nullable = false)
    val flashSaleId: Long,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Column(name = "sku_id", nullable = false, length = 50)
    val skuId: String,

    @Column(nullable = false)
    val quantity: Int,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var status: ReservationStatus = ReservationStatus.RESERVED,

    @Column(name = "order_id")
    var orderId: Long? = null,

    @Column(name = "expires_at", nullable = false)
    val expiresAt: LocalDateTime,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),
) {

    fun confirm(orderId: Long) {
        check(status == ReservationStatus.RESERVED) {
            "Cannot confirm reservation in status=$status"
        }
        this.status = ReservationStatus.CONFIRMED
        this.orderId = orderId
        this.updatedAt = LocalDateTime.now()
    }

    fun release() {
        check(status == ReservationStatus.RESERVED) {
            "Cannot release reservation in status=$status"
        }
        this.status = ReservationStatus.RELEASED
        this.updatedAt = LocalDateTime.now()
    }

    fun expire() {
        check(status == ReservationStatus.RESERVED) {
            "Cannot expire reservation in status=$status"
        }
        this.status = ReservationStatus.EXPIRED
        this.updatedAt = LocalDateTime.now()
    }

    fun isExpired(): Boolean = status == ReservationStatus.RESERVED &&
        LocalDateTime.now().isAfter(expiresAt)
}
