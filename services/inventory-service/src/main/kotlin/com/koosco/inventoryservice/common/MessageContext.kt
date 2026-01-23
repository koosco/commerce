package com.koosco.inventoryservice.common

/**
 * fileName       : MessageContext
 * author         : koo
 * date           : 2025. 12. 22. 오전 10:50
 * description    :
 */
data class MessageContext(val correlationId: String, val causationId: String?)
