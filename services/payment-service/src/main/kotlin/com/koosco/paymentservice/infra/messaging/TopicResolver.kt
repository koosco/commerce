package com.koosco.paymentservice.infra.messaging

import com.koosco.paymentservice.contract.PaymentIntegrationEvent

interface TopicResolver {
    fun resolve(event: PaymentIntegrationEvent): String
}
