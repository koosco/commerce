package com.koosco.common.core.event

import kotlin.reflect.KClass

/**
 * Abstract base class for handling domain events with automatic validation and lifecycle hooks.
 * This class provides a template for implementing domain event handlers that process DomainEvents
 * with automatic type checking and consistent event processing flow.
 *
 * Subclasses must implement:
 * - [handleInternal]: Core business logic for processing the event
 * - [supportedType]: The specific DomainEvent type this handler supports
 *
 * Subclasses can optionally override:
 * - [validate]: Custom validation logic before event processing
 * - [afterHandle]: Post-processing logic after event handling completes
 *
 * Features:
 * - Automatic type checking in [handle] method
 * - Silently ignores events that don't match the supported type
 * - Pre-processing validation hook
 * - Template method pattern for consistent event handling flow
 * - Post-processing hook for cleanup or follow-up actions
 * - Type-safe event handling with generic type parameter
 *
 * Example implementation:
 * ```
 * @Component
 * class OrderCreatedEventHandler : AbstractDomainEventHandler<OrderCreatedEvent>() {
 *     override val supportedType: KClass<OrderCreatedEvent> = OrderCreatedEvent::class
 *
 *     override fun handleInternal(event: OrderCreatedEvent) {
 *         log.info("Processing order: ${event.orderId}")
 *         // Business logic here
 *         orderService.processOrder(event.orderId)
 *     }
 *
 *     override fun validate(event: OrderCreatedEvent) {
 *         require(event.orderId.isNotBlank()) { "Order ID cannot be blank" }
 *         require(event.totalAmount > BigDecimal.ZERO) { "Total amount must be positive" }
 *     }
 *
 *     override fun afterHandle(event: OrderCreatedEvent) {
 *         log.info("Completed processing order event: ${event.eventId}")
 *         metricsService.recordEventProcessed(event.getEventType())
 *     }
 * }
 * ```
 *
 * @param T The specific DomainEvent type this handler processes
 */
abstract class AbstractDomainEventHandler<T : DomainEvent> : EventHandler<T> {
    /**
     * The Kotlin class type that this handler supports.
     * This is used for runtime type checking in [handle].
     */
    protected abstract val supportedType: KClass<T>

    /**
     * Handles the domain event with automatic type checking and consistent lifecycle:
     * 0. Type checking - silently returns if event type doesn't match [supportedType]
     * 1. Pre-processing validation via [validate]
     * 2. Core business logic via [handleInternal]
     * 3. Post-processing via [afterHandle] (always called, even on failure)
     *
     * This method is marked final to enforce the consistent handling pattern.
     * Subclasses should override [handleInternal] instead.
     *
     * @param event The domain event to handle
     * @throws EventHandlingException if handling fails
     */
    final override fun handle(event: T) {
        // 0. Type checking - ignore if not supported
        if (!canHandle(event)) {
            return
        }

        try {
            // 1. Pre-processing validation
            validate(event)

            // 2. Core business logic
            handleInternal(event)
        } catch (e: EventHandlingException) {
            throw e
        } catch (e: Exception) {
            throw EventHandlingException("Failed to handle event: ${event.getEventType()}", e)
        } finally {
            // 3. Post-processing (always called)
            try {
                afterHandle(event)
            } catch (e: Exception) {
                // Log but don't propagate afterHandle exceptions
                // to avoid masking the original exception
            }
        }
    }

    /**
     * Check if this handler can process the given event.
     * Default implementation checks if the event's class matches [supportedType].
     *
     * This method is protected and used internally by [handle] for type checking.
     * Override this method if you need custom type checking logic.
     *
     * @param event The event to check
     * @return true if this handler can process the event
     */
    protected open fun canHandle(event: T): Boolean = event::class == supportedType

    /**
     * Core business logic for handling the event.
     * Subclasses must implement this method with their specific event processing logic.
     *
     * @param event The domain event to process
     * @throws Exception if processing fails
     */
    protected abstract fun handleInternal(event: T)

    /**
     * Pre-processing validation hook.
     * Override this method to add custom validation logic before event processing.
     * Default implementation does nothing.
     *
     * @param event The event to validate
     * @throws IllegalArgumentException if validation fails
     * @throws IllegalStateException if system state is invalid for processing
     */
    protected open fun validate(event: T) {
        // Default: no additional validation
    }

    /**
     * Post-processing hook.
     * Override this method to add cleanup logic or follow-up actions after event processing.
     * Default implementation does nothing.
     *
     * This method is called even if [handleInternal] throws an exception,
     * similar to a finally block.
     *
     * @param event The event that was processed
     */
    protected open fun afterHandle(event: T) {
        // Default: no post-processing
    }
}
