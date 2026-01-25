package com.koosco.common.core.messaging

data class MessageContext(
    val correlationId: String,
    val causationId: String?,
)
