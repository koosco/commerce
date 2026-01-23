package com.koosco.userservice.domain.event

import java.time.LocalDateTime

interface DomainEvent {
    val occurredAt: LocalDateTime
}
