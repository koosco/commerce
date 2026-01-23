package com.koosco.paymentservice.common

/**
 * fileName       : MessageContext
 * author         : koo
 * date           : 2025. 12. 24. 오전 6:02
 * description    :
 */
data class MessageContext(val correlationId: String, val causationId: String?)
