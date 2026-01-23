package com.koosco.authservice.domain.entity

import com.koosco.authservice.domain.enums.UserRole
import com.koosco.authservice.domain.vo.AuthProvider
import com.koosco.authservice.domain.vo.Email
import com.koosco.authservice.domain.vo.EncryptedPassword
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "user_auth")
class UserAuth(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    val userId: Long,

    @Column(nullable = false)
    val email: Email,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val provider: AuthProvider = AuthProvider.LOCAL,

    @Column(nullable = false)
    var password: EncryptedPassword,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val role: UserRole = UserRole.ROLE_USER,

    var refreshToken: String? = null,

    val createdAt: LocalDateTime = LocalDateTime.now(),

    var updatedAt: LocalDateTime = LocalDateTime.now(),
) {
    companion object {
        fun createUser(userId: Long, email: Email, password: EncryptedPassword, provider: AuthProvider?): UserAuth =
            UserAuth(
                id = null,
                userId = userId,
                email = email,
                provider = provider ?: AuthProvider.LOCAL,
                password = password,
                role = UserRole.ROLE_USER,
            )
    }

    fun storeRefreshToken(token: String) {
        this.refreshToken = token
    }
}
