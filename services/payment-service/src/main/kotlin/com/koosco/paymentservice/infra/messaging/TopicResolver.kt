package com.koosco.paymentservice.infra.messaging

import com.koosco.paymentservice.application.contract.PaymentIntegrationEvent

interface TopicResolver {
    fun resolve(event: PaymentIntegrationEvent): String
}
