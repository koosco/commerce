package com.koosco.common.core.outbox

/**
 * Status of an outbox entry.
 *
 * When using CDC (Change Data Capture) with Debezium:
 * - Entries are created with PENDING status
 * - Debezium reads the binlog and publishes to Kafka
 * - After successful publish, the entry can be deleted or marked as PUBLISHED
 *
 * When using polling-based outbox:
 * - A scheduler polls for PENDING entries
 * - After publishing, marks as PUBLISHED
 * - FAILED status is used for entries that exceeded retry attempts
 */
enum class OutboxStatus {
    /**
     * Entry is waiting to be published to Kafka.
     */
    PENDING,

    /**
     * Entry has been successfully published to Kafka.
     * Used when entries are kept for audit/debugging purposes.
     */
    PUBLISHED,

    /**
     * Entry failed to publish after all retry attempts.
     * Requires manual intervention or dead-letter queue handling.
     */
    FAILED,
}
