package com.koosco.userservice.domain.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "login_history",
    indexes = [
        Index(name = "idx_login_history_user_id", columnList = "user_id, login_at"),
    ],
)
class LoginHistory(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Column(nullable = false)
    val ip: String,

    @Column(name = "user_agent", nullable = true, length = 512)
    val userAgent: String?,

    @Column(nullable = false)
    val success: Boolean,

    @Column(name = "failure_reason", nullable = true)
    val failureReason: String? = null,

    @Column(name = "login_at", nullable = false)
    val loginAt: LocalDateTime = LocalDateTime.now(),
) {
    companion object {
        fun success(userId: Long, ip: String, userAgent: String?): LoginHistory = LoginHistory(
            userId = userId,
            ip = ip,
            userAgent = userAgent,
            success = true,
        )

        fun failure(userId: Long, ip: String, userAgent: String?, reason: String): LoginHistory = LoginHistory(
            userId = userId,
            ip = ip,
            userAgent = userAgent,
            success = false,
            failureReason = reason,
        )
    }
}
