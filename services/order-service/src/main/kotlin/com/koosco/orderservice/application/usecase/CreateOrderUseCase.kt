package com.koosco.orderservice.application.usecase

import com.koosco.common.core.annotation.UseCase
import com.koosco.orderservice.application.command.CreateOrderCommand
import com.koosco.orderservice.application.port.IntegrationEventProducer
import com.koosco.orderservice.application.port.OrderRepository
import com.koosco.orderservice.application.result.CreateOrderResult
import com.koosco.orderservice.contract.outbound.order.OrderPlacedEvent
import com.koosco.orderservice.contract.outbound.order.OrderPlacedEvent.PlacedItem
import com.koosco.orderservice.domain.entity.Order
import com.koosco.orderservice.domain.vo.OrderAmount
import com.koosco.orderservice.domain.vo.OrderItemSpec
import com.koosco.orderservice.domain.vo.ShippingAddress
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

/**
 * 주문 생성 flow
 *
 * trigger = api call
 *
 * 1) payment service 결제 초기화
 * - No Feedback
 *
 * 2) inventory service 재고 예약
 * - success = MarkOrderPaymentPendingUseCase
 * - fail = retry & dlq
 */
@UseCase
class CreateOrderUseCase(
    private val orderRepository: OrderRepository,
    private val integrationEventProducer: IntegrationEventProducer,
) {

    @Transactional
    fun execute(command: CreateOrderCommand): CreateOrderResult {
        // 구매 상품 정보 생성
        val itemSpecs = command.items.map {
            OrderItemSpec(
                skuId = it.skuId,
                quantity = it.quantity,
                unitPrice = it.unitPrice,
            )
        }

        // 주문 금액 계산
        val orderAmount = OrderAmount.from(
            itemSpecs = itemSpecs,
            discount = command.discountAmount,
        )

        // 배송지 스냅샷
        val shippingAddress = ShippingAddress(
            recipient = command.shippingAddress.recipient,
            phone = command.shippingAddress.phone,
            zipCode = command.shippingAddress.zipCode,
            address = command.shippingAddress.address,
            addressDetail = command.shippingAddress.addressDetail,
        )

        // 주문 생성
        val order = Order.create(
            userId = command.userId,
            itemSpecs = itemSpecs,
            amount = orderAmount,
            shippingAddress = shippingAddress,
        )

        val savedOrder = orderRepository.save(order)

        savedOrder.place()

        // Integration event 직접 생성 및 발행
        integrationEventProducer.publish(
            OrderPlacedEvent(
                orderId = savedOrder.id!!,
                userId = savedOrder.userId,
                payableAmount = savedOrder.payableAmount.amount,
                items = savedOrder.items.map {
                    PlacedItem(
                        skuId = it.skuId,
                        quantity = it.quantity,
                        unitPrice = it.unitPrice.amount,
                    )
                },
                correlationId = savedOrder.id.toString(),
                causationId = UUID.randomUUID().toString(),
            ),
        )

        return CreateOrderResult(
            orderId = savedOrder.id!!,
            status = savedOrder.status,
            payableAmount = savedOrder.payableAmount.amount,
        )
    }
}
