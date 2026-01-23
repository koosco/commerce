package com.koosco.userservice.domain.entity

import com.koosco.common.core.exception.ConflictException
import com.koosco.userservice.common.UserErrorCode
import com.koosco.userservice.domain.enums.AuthProvider
import com.koosco.userservice.domain.enums.UserRole
import com.koosco.userservice.domain.enums.UserStatus
import com.koosco.userservice.domain.vo.Email
import com.koosco.userservice.domain.vo.Phone
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "users")
class User(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    val email: Email,

    @Column(nullable = false, unique = true)
    var name: String,

    @Column(nullable = true)
    var phone: Phone,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var status: UserStatus = UserStatus.ACTIVE,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val role: UserRole = UserRole.ROLE_USER,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val provider: AuthProvider = AuthProvider.LOCAL,

    val createdAt: LocalDateTime = LocalDateTime.now(),

    var updatedAt: LocalDateTime = LocalDateTime.now(),
) {
    companion object {
        fun createUser(email: Email, name: String, phone: Phone, provider: AuthProvider): User = User(
            email = email,
            name = name,
            phone = phone,
            provider = provider,
            role = UserRole.ROLE_USER,
        )
    }

    fun update(name: String?, phone: Phone?) {
        this.name = name ?: this.name
        this.phone = phone ?: this.phone
        this.updatedAt = LocalDateTime.now()
    }

    fun quit() {
        this.status = UserStatus.INACTIVE
        this.updatedAt = LocalDateTime.now()
    }

    fun activate() {
        this.status = UserStatus.ACTIVE
        this.updatedAt = LocalDateTime.now()
    }

    fun forceDelete() {
        if (status === UserStatus.BLOCKED) {
            throw ConflictException(UserErrorCode.USER_ALREADY_DELETED)
        }
        this.status = UserStatus.BLOCKED
        this.updatedAt = LocalDateTime.now()
    }
}
