package com.koosco.orderservice.application.usecase

import com.fasterxml.jackson.databind.ObjectMapper
import com.koosco.common.core.annotation.UseCase
import com.koosco.common.core.event.IntegrationEventProducer
import com.koosco.orderservice.application.command.CreateOrderCommand
import com.koosco.orderservice.application.port.OrderIdempotencyRepository
import com.koosco.orderservice.application.port.OrderRepository
import com.koosco.orderservice.application.port.OrderStatusHistoryRepository
import com.koosco.orderservice.application.result.CreateOrderResult
import com.koosco.orderservice.contract.outbound.order.OrderPlacedEvent
import com.koosco.orderservice.contract.outbound.order.OrderPlacedEvent.PlacedItem
import com.koosco.orderservice.domain.entity.Order
import com.koosco.orderservice.domain.entity.OrderIdempotency
import com.koosco.orderservice.domain.entity.OrderStatusHistory
import com.koosco.orderservice.domain.enums.OrderStatus
import com.koosco.orderservice.domain.vo.OrderAmount
import com.koosco.orderservice.domain.vo.OrderItemSpec
import com.koosco.orderservice.domain.vo.PricingSnapshot
import com.koosco.orderservice.domain.vo.ShippingAddress
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@UseCase
class CreateOrderUseCase(
    private val orderRepository: OrderRepository,
    private val orderStatusHistoryRepository: OrderStatusHistoryRepository,
    private val orderIdempotencyRepository: OrderIdempotencyRepository,
    private val integrationEventProducer: IntegrationEventProducer,
    private val objectMapper: ObjectMapper,
) {

    @Transactional
    fun execute(command: CreateOrderCommand): CreateOrderResult {
        // 멱등성 체크
        if (command.idempotencyKey != null) {
            val existing = orderIdempotencyRepository.findByUserIdAndIdempotencyKey(
                command.userId,
                command.idempotencyKey,
            )
            if (existing != null) {
                val order = orderRepository.findById(existing.orderId)!!
                return CreateOrderResult(
                    orderId = order.id!!,
                    orderNo = order.orderNo,
                    status = order.status,
                    totalAmount = order.totalAmount.amount,
                )
            }
        }

        val itemSpecs = command.items.map {
            OrderItemSpec(
                skuId = it.skuId,
                productId = it.productId,
                brandId = it.brandId,
                titleSnapshot = it.titleSnapshot,
                optionSnapshot = it.optionSnapshot,
                quantity = it.quantity,
                unitPrice = it.unitPrice,
            )
        }

        val orderAmount = OrderAmount.from(
            itemSpecs = itemSpecs,
            discount = command.discountAmount,
            shippingFee = command.shippingFee,
        )

        val shippingAddress = ShippingAddress(
            recipient = command.shippingAddress.recipient,
            phone = command.shippingAddress.phone,
            zipCode = command.shippingAddress.zipCode,
            address = command.shippingAddress.address,
            addressDetail = command.shippingAddress.addressDetail,
        )

        val pricingSnapshot = PricingSnapshot(
            subtotal = orderAmount.subtotal.amount,
            discount = orderAmount.discount.amount,
            shippingFee = orderAmount.shippingFee.amount,
            total = orderAmount.total.amount,
            items = itemSpecs.map {
                PricingSnapshot.PricingSnapshotItem(
                    skuId = it.skuId,
                    unitPrice = it.unitPrice.amount,
                    qty = it.quantity,
                    lineAmount = it.totalPrice().amount,
                )
            },
        )

        val orderNo = generateOrderNo()

        val order = Order.create(
            orderNo = orderNo,
            userId = command.userId,
            itemSpecs = itemSpecs,
            amount = orderAmount,
            shippingAddressSnapshot = objectMapper.writeValueAsString(shippingAddress),
            pricingSnapshot = objectMapper.writeValueAsString(pricingSnapshot),
        )

        val savedOrder = orderRepository.save(order)

        // 상태 이력 기록
        orderStatusHistoryRepository.save(
            OrderStatusHistory.create(
                orderId = savedOrder.id!!,
                fromStatus = null,
                toStatus = OrderStatus.CREATED,
            ),
        )

        // API 멱등성 기록
        if (command.idempotencyKey != null) {
            orderIdempotencyRepository.save(
                OrderIdempotency.create(
                    userId = command.userId,
                    idempotencyKey = command.idempotencyKey,
                    orderId = savedOrder.id!!,
                ),
            )
        }

        integrationEventProducer.publish(
            OrderPlacedEvent(
                orderId = savedOrder.id!!,
                userId = savedOrder.userId,
                payableAmount = savedOrder.totalAmount.amount,
                items = savedOrder.items.map {
                    PlacedItem(
                        skuId = it.skuId,
                        quantity = it.qty,
                        unitPrice = it.unitPrice.amount,
                    )
                },
                correlationId = savedOrder.id.toString(),
                causationId = UUID.randomUUID().toString(),
            ),
        )

        return CreateOrderResult(
            orderId = savedOrder.id!!,
            orderNo = savedOrder.orderNo,
            status = savedOrder.status,
            totalAmount = savedOrder.totalAmount.amount,
        )
    }

    private fun generateOrderNo(): String {
        val now = LocalDateTime.now()
        val datePart = "%04d%02d%02d".format(now.year, now.monthValue, now.dayOfMonth)
        val randomPart = UUID.randomUUID().toString().replace("-", "").take(16).uppercase()
        return "ORD-$datePart-$randomPart"
    }
}
