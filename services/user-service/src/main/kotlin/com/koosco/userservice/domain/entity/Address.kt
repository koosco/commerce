package com.koosco.userservice.domain.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "member_address")
class Address(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val member: Member,

    @Column(nullable = false)
    val label: String,

    @Column(nullable = false)
    val recipient: String,

    @Column(nullable = false)
    val phone: String,

    @Column(nullable = false)
    val zipCode: String,

    @Column(nullable = false)
    val address: String,

    @Column(nullable = false)
    val addressDetail: String,

    @Column(nullable = false)
    var isDefault: Boolean = false,

    val createdAt: LocalDateTime = LocalDateTime.now(),

    var updatedAt: LocalDateTime = LocalDateTime.now(),
) {
    companion object {
        fun create(
            member: Member,
            label: String,
            recipient: String,
            phone: String,
            zipCode: String,
            address: String,
            addressDetail: String,
            isDefault: Boolean,
        ): Address = Address(
            member = member,
            label = label,
            recipient = recipient,
            phone = phone,
            zipCode = zipCode,
            address = address,
            addressDetail = addressDetail,
            isDefault = isDefault,
        )
    }

    fun clearDefault() {
        isDefault = false
        updatedAt = LocalDateTime.now()
    }
}
