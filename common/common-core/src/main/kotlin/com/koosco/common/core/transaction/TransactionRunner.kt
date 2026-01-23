package com.koosco.common.core.transaction

import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

/**
 * Interface for executing code within Spring transactions.
 *
 * This interface provides convenient methods for running code blocks within different
 * transaction contexts. Implementations are automatically registered via Auto Configuration
 * when common-core is included as a dependency.
 *
 * Example usage:
 * ```
 * @Service
 * class OrderService(
 *     private val transactionRunner: TransactionRunner
 * ) {
 *     fun createOrder(order: Order) {
 *         transactionRunner.run {
 *             // Code runs within a transaction
 *             orderRepository.save(order)
 *         }
 *     }
 * }
 * ```
 */
interface TransactionRunner {

    /**
     * Execute the given function within a transaction.
     * Uses default transaction propagation (REQUIRED).
     *
     * @param func The function to execute
     * @return The result of the function
     */
    fun <T> run(func: () -> T): T

    /**
     * Execute the given function within a read-only transaction.
     * Optimized for read operations, may improve performance.
     *
     * @param func The function to execute
     * @return The result of the function
     */
    fun <T> readOnly(func: () -> T): T

    /**
     * Execute the given function within a new transaction.
     * Suspends the current transaction (if any) and creates a new one.
     * Uses REQUIRES_NEW propagation.
     *
     * @param func The function to execute
     * @return The result of the function
     */
    fun <T> runNew(func: () -> T): T
}

/**
 * Default implementation of TransactionRunner.
 * Registered automatically via CommonCoreAutoConfiguration.
 *
 * This class is marked as open to allow Spring's CGLIB proxy to create a subclass
 * for @Transactional annotation support.
 */
open class TransactionRunnerImpl : TransactionRunner {

    @Transactional
    override fun <T> run(func: () -> T): T = func()

    @Transactional(readOnly = true)
    override fun <T> readOnly(func: () -> T): T = func()

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun <T> runNew(func: () -> T): T = func()
}
