package com.koosco.userservice.domain.entity

import com.koosco.common.core.exception.ConflictException
import com.koosco.userservice.common.MemberErrorCode
import com.koosco.userservice.domain.enums.MemberRole
import com.koosco.userservice.domain.enums.MemberStatus
import com.koosco.userservice.domain.vo.Email
import com.koosco.userservice.domain.vo.EncryptedPassword
import com.koosco.userservice.domain.vo.Phone
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "member_user")
class Member(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    var id: Long? = null,

    @Column(nullable = false)
    val email: Email,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = true)
    var phone: Phone?,

    @Column(name = "password_hash", nullable = true)
    var passwordHash: EncryptedPassword?,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val role: MemberRole = MemberRole.USER,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var status: MemberStatus = MemberStatus.ACTIVE,

    val createdAt: LocalDateTime = LocalDateTime.now(),

    var updatedAt: LocalDateTime = LocalDateTime.now(),
) {
    companion object {
        fun create(email: Email, name: String, phone: Phone?, passwordHash: EncryptedPassword?): Member = Member(
            email = email,
            name = name,
            phone = phone,
            passwordHash = passwordHash,
        )
    }

    fun update(name: String?, phone: Phone?) {
        this.name = name ?: this.name
        this.phone = phone ?: this.phone
        this.updatedAt = LocalDateTime.now()
    }

    fun withdraw() {
        if (status == MemberStatus.WITHDRAWN) {
            throw ConflictException(MemberErrorCode.MEMBER_ALREADY_WITHDRAWN)
        }
        this.status = MemberStatus.WITHDRAWN
        this.updatedAt = LocalDateTime.now()
    }

    fun lock() {
        if (status == MemberStatus.LOCKED) {
            throw ConflictException(MemberErrorCode.MEMBER_ALREADY_LOCKED)
        }
        this.status = MemberStatus.LOCKED
        this.updatedAt = LocalDateTime.now()
    }
}
