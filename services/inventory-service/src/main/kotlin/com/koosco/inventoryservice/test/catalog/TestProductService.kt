package com.koosco.inventoryservice.test.catalog

import com.koosco.common.core.event.CloudEvent
import com.koosco.inventoryservice.application.contract.inbound.catalog.ProductSkuCreatedEvent
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

/**
 * fileName       : TestProductService
 * author         : koo
 * date           : 2025. 12. 27. 오후 6:52
 * description    : Integration Event 발행 테스트를 위한 Service, local profile only
 */
@Profile("local")
@Service
class TestProductService(
    private val kafkaTemplate: KafkaTemplate<String, CloudEvent<*>>,

    @Value("\${inventory.topic.mappings.product.sku.created}")
    private val productCreatedTopic: String,
) {

    companion object {
        val PRODUCTS = listOf(
            TestProduct(
                skuId = "12341f4c-a36c-4a70-9347-413ce52d1234",
                productId = 1001L,
                productCode = "TSH-BLK-M",
                price = 29900L,
                optionValues = """{"color": "Black", "size": "M"}""",
                initialQuantity = 10000,
            ),
            TestProduct(
                skuId = "1235298f-0c73-4df1-8576-ac2326871235",
                productId = 1001L,
                productCode = "TSH-BLK-L",
                price = 29900L,
                optionValues = """{"color": "Black", "size": "L"}""",
                initialQuantity = 10000,
            ),
        )
    }

    @Transactional
    fun execute(): List<ProductSkuCreatedEvent> {
        val skuCreatedEvents = PRODUCTS.map {
            ProductSkuCreatedEvent(
                it.skuId,
                it.productId,
                it.productCode,
                it.price,
                it.optionValues,
                it.initialQuantity,
                LocalDateTime.now(),
            )
        }

        val events = skuCreatedEvents.map {
            CloudEvent.of(
                source = "catalog-service.test-product",
                type = "product.sku.created",
                data = it,
            )
        }.toList()

        events.forEach { kafkaTemplate.send(productCreatedTopic, it) }

        return skuCreatedEvents
    }
}

data class TestProduct(
    val skuId: String,
    val productId: Long,
    val productCode: String,
    val price: Long,
    val optionValues: String,
    val initialQuantity: Int,
)
