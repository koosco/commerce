package com.koosco.common.core.event

/**
 * Interface for handling domain events.
 * Implementations should be registered as Spring beans and will be auto-discovered.
 *
 * The handler implementation should determine internally whether it can process
 * the given event type. If the event cannot be handled, the implementation should
 * either silently ignore it or throw an appropriate exception.
 *
 * Example:
 * ```
 * @Component
 * class OrderCreatedEventHandler : EventHandler<OrderCreatedEvent> {
 *     override fun handle(event: OrderCreatedEvent) {
 *         // Type checking is done by the framework/dispatcher
 *         // Just implement the business logic
 *         log.info("Order created: ${event.orderId}")
 *         orderService.processOrder(event)
 *     }
 * }
 * ```
 */
interface EventHandler<T : DomainEvent> {
    /**
     * Handle the domain event.
     * The implementation should check internally if it can process the event type.
     *
     * @param event The domain event to handle
     * @throws EventHandlingException if handling fails
     */
    fun handle(event: T)

    /**
     * Get the order of this handler.
     * Lower values have higher priority.
     * Default is 0.
     */
    fun getOrder(): Int = 0
}

/**
 * Interface for handling CloudEvents directly.
 * Use this when you need to work with the CloudEvent wrapper.
 */
interface CloudEventHandler<T> {
    /**
     * Handle the CloudEvent.
     *
     * @param event The CloudEvent to handle
     * @throws EventHandlingException if handling fails
     */
    fun handle(event: CloudEvent<T>)

    /**
     * Get the order of this handler.
     * Lower values have higher priority.
     * Default is 0.
     */
    fun getOrder(): Int = 0
}

/**
 * Exception thrown when event handling fails.
 */
class EventHandlingException(
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)

/**
 * Annotation for marking event handler methods.
 * Can be used with Spring's component scanning.
 *
 * Example:
 * ```
 * @Component
 * class OrderEventHandlers {
 *     @EventListener(eventType = "com.koosco.order.created")
 *     fun handleOrderCreated(event: OrderCreatedEvent) {
 *         // Handle event
 *     }
 * }
 * ```
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class EventListener(
    /**
     * The event type this listener handles.
     */
    val eventType: String = "",
    /**
     * The order of execution. Lower values execute first.
     */
    val order: Int = 0,
)
