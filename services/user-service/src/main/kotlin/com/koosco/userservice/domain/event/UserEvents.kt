package com.koosco.userservice.domain.event

import java.time.LocalDateTime

data class UserRegistered(
    val userId: Long,
    val email: String,
    val name: String,
    val phone: String?,
    override val occurredAt: LocalDateTime = LocalDateTime.now(),
) : DomainEvent

data class UserDeleted(val userId: Long, override val occurredAt: LocalDateTime = LocalDateTime.now()) : DomainEvent
