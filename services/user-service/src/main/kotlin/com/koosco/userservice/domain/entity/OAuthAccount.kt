package com.koosco.userservice.domain.entity

import com.koosco.userservice.domain.enums.OAuthProvider
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "member_oauth_account")
class OAuthAccount(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "oauth_id")
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val member: Member,

    @Enumerated(EnumType.STRING)
    val provider: OAuthProvider,

    @Column(name = "provider_user_id")
    val providerUserId: String,

    val createdAt: LocalDateTime = LocalDateTime.now(),
)
