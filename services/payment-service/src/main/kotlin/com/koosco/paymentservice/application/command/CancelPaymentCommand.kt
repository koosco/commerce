package com.koosco.paymentservice.application.command

import java.util.UUID

data class CancelPaymentCommand(val paymentId: UUID, val cancelAmount: Long)
