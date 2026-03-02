package com.koosco.paymentservice.application.usecase

import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.event.IntegrationEventProducer
import com.koosco.common.core.exception.BadRequestException
import com.koosco.common.core.exception.NotFoundException
import com.koosco.common.core.messaging.MessageContext
import com.koosco.paymentservice.application.command.CreatePaymentCommand
import com.koosco.paymentservice.application.port.OrderQueryPort
import com.koosco.paymentservice.application.port.PaymentRepository
import com.koosco.paymentservice.common.PaymentErrorCode
import com.koosco.paymentservice.contract.outbound.payment.PaymentCreatedEvent
import com.koosco.paymentservice.domain.entity.Payment
import com.koosco.paymentservice.domain.vo.Money
import org.slf4j.LoggerFactory
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@UseCase
class CreatePaymentUseCase(
    private val paymentRepository: PaymentRepository,
    private val integrationEventProducer: IntegrationEventProducer,
    private val orderQueryPort: OrderQueryPort,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    fun execute(command: CreatePaymentCommand, context: MessageContext) {
        if (paymentRepository.existsByOrderId(command.orderId)) {
            return
        }

        val order = orderQueryPort.getOrder(command.orderId)
            ?: throw NotFoundException(
                PaymentErrorCode.PAYMENT_NOT_FOUND,
                "주문 정보를 찾을 수 없습니다. orderId=${command.orderId}",
            )

        if (order.status != "PAYMENT_PENDING") {
            logger.warn(
                "결제 가능한 주문 상태가 아닙니다. orderId={}, status={}",
                command.orderId,
                order.status,
            )
            throw BadRequestException(
                PaymentErrorCode.PAYMENT_NOT_READY,
                "결제 가능한 주문 상태가 아닙니다. status=${order.status}",
            )
        }

        if (order.totalAmount != command.amount) {
            logger.warn(
                "주문 금액과 결제 금액이 불일치합니다. orderId={}, orderAmount={}, paymentAmount={}",
                command.orderId,
                order.totalAmount,
                command.amount,
            )
            throw BadRequestException(
                PaymentErrorCode.INVALID_PAYMENT_AMOUNT,
                "주문 금액과 결제 금액이 불일치합니다. orderAmount=${order.totalAmount}, paymentAmount=${command.amount}",
            )
        }

        if (order.userId != command.userId) {
            logger.warn(
                "주문자와 결제 요청자가 불일치합니다. orderId={}, orderUserId={}, paymentUserId={}",
                command.orderId,
                order.userId,
                command.userId,
            )
            throw BadRequestException(
                PaymentErrorCode.INVALID_PAYMENT_REQUEST,
                "주문자와 결제 요청자가 불일치합니다.",
            )
        }

        val savedPayment = paymentRepository.save(
            Payment(
                paymentId = UUID.randomUUID(),
                orderId = command.orderId,
                userId = command.userId,
                amount = Money(command.amount),
            ),
        )

        integrationEventProducer.publish(
            PaymentCreatedEvent(savedPayment.paymentId.toString(), savedPayment.orderId),
        )
    }
}
