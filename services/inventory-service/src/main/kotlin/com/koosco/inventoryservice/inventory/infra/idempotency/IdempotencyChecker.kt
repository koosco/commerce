package com.koosco.inventoryservice.inventory.infra.idempotency

import com.koosco.inventoryservice.inventory.domain.entity.InventoryEventIdempotency
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Component

/**
 * Helper for idempotency operations in inventory-service.
 *
 * Provides a consistent pattern for checking and recording processed events.
 */
@Component
class IdempotencyChecker(private val idempotencyRepository: InventoryIdempotencyRepository) {
    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * Check if an event has already been processed.
     * Use this for fast-path rejection in consumers.
     *
     * @return true if already processed, false otherwise
     */
    fun isAlreadyProcessed(eventId: String, action: String): Boolean =
        idempotencyRepository.existsByEventIdAndAction(eventId, action)

    /**
     * Record that an event has been processed.
     * Should be called within the same transaction as the business logic.
     *
     * @return true if successfully recorded, false if already exists (duplicate)
     */
    fun recordProcessed(eventId: String, action: String, referenceId: String): Boolean = try {
        idempotencyRepository.save(
            InventoryEventIdempotency.create(
                eventId = eventId,
                action = action,
                referenceId = referenceId,
            ),
        )
        true
    } catch (e: DataIntegrityViolationException) {
        // Unique constraint violation - already processed by another thread
        logger.info(
            "Event already processed (race condition resolved): eventId=$eventId, action=$action",
        )
        false
    }
}
