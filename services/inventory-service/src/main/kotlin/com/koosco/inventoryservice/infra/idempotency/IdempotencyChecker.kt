package com.koosco.inventoryservice.infra.idempotency

import com.koosco.inventoryservice.domain.entity.InventoryEventIdempotency
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Component

@Component
class IdempotencyChecker(private val idempotencyRepository: InventoryIdempotencyRepository) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun isAlreadyProcessed(messageId: String, action: String): Boolean =
        idempotencyRepository.existsByMessageIdAndAction(messageId, action)

    fun recordProcessed(messageId: String, action: String, aggregateId: String): Boolean = try {
        idempotencyRepository.save(
            InventoryEventIdempotency.create(
                messageId = messageId,
                action = action,
                aggregateId = aggregateId,
            ),
        )
        true
    } catch (e: DataIntegrityViolationException) {
        logger.info(
            "Event already processed (race condition resolved): messageId=$messageId, action=$action",
        )
        false
    }
}
