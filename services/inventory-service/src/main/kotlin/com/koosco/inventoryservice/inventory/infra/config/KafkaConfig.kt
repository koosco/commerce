package com.koosco.inventoryservice.inventory.infra.config

import com.koosco.common.core.event.CloudEvent
import com.koosco.common.core.exception.BaseException
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.*
import org.springframework.kafka.listener.ConsumerAwareRebalanceListener
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer
import org.springframework.kafka.listener.DefaultErrorHandler
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.support.serializer.JsonSerializer
import org.springframework.util.backoff.ExponentialBackOff

@EnableKafka
@Configuration
class KafkaConfig(private val kafkaProperties: KafkaProperties) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Bean
    fun producerFactory(): ProducerFactory<String, CloudEvent<*>> {
        val props = kafkaProperties.buildProducerProperties(null).toMutableMap()

        // JsonSerializer 설정
        val jsonSerializer = JsonSerializer<CloudEvent<*>>().apply {
            setAddTypeInfo(false)
        }

        return DefaultKafkaProducerFactory(
            props,
            StringSerializer(),
            jsonSerializer,
        )
    }

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, CloudEvent<*>> = KafkaTemplate(producerFactory())

    @Bean
    fun consumerFactory(): ConsumerFactory<String, CloudEvent<*>> {
        val props = kafkaProperties.buildConsumerProperties(null).toMutableMap()

        props[ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG] =
            "org.apache.kafka.clients.consumer.CooperativeStickyAssignor"

        // JsonDeserializer를 명시적 타입과 함께 생성
        val jsonDeserializer = JsonDeserializer(CloudEvent::class.java).apply {
            addTrustedPackages("*")
            setUseTypeHeaders(false)
        }

        return DefaultKafkaConsumerFactory(
            props,
            StringDeserializer(),
            jsonDeserializer,
        )
    }

    @Bean
    fun deadLetterPublishingRecoverer(): DeadLetterPublishingRecoverer {
        val recoverer = DeadLetterPublishingRecoverer(kafkaTemplate()) { record, _ ->
            TopicPartition("${record.topic()}.DLT", record.partition())
        }
        return recoverer
    }

    @Bean
    fun kafkaErrorHandler(): DefaultErrorHandler {
        val backOff = ExponentialBackOff().apply {
            initialInterval = 1_000L
            multiplier = 2.0
            maxInterval = 10_000L
            maxAttempts = MAX_RETRY_ATTEMPTS
        }

        return DefaultErrorHandler(deadLetterPublishingRecoverer(), backOff).apply {
            addNotRetryableExceptions(BaseException::class.java)
            setCommitRecovered(true)
            setRetryListeners(
                { record, ex, deliveryAttempt ->
                    log.warn(
                        "Retry attempt {} for topic={}, partition={}, offset={}: {}",
                        deliveryAttempt,
                        record.topic(),
                        record.partition(),
                        record.offset(),
                        ex.message,
                    )
                },
            )
        }
    }

    companion object {
        private const val MAX_RETRY_ATTEMPTS = 3
    }

    @Bean
    fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, CloudEvent<*>> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, CloudEvent<*>>()
        factory.consumerFactory = consumerFactory()
        factory.containerProperties.ackMode =
            org.springframework.kafka.listener.ContainerProperties.AckMode.MANUAL_IMMEDIATE
        factory.containerProperties.setConsumerRebalanceListener(inventoryRebalanceListener())
        factory.setCommonErrorHandler(kafkaErrorHandler())
        return factory
    }

    @Bean
    fun inventoryRebalanceListener(): ConsumerAwareRebalanceListener = object : ConsumerAwareRebalanceListener {
        override fun onPartitionsRevokedBeforeCommit(
            consumer: Consumer<*, *>,
            partitions: Collection<TopicPartition>,
        ) {
            log.warn("Rebalance started - before partition revoke: {}", partitions)
            try {
                consumer.commitSync()
                log.info("Offset commit completed before revoke")
            } catch (e: Exception) {
                log.error("Offset commit failed: {}", e.message, e)
            }
        }

        override fun onPartitionsRevoked(partitions: Collection<TopicPartition>) {
            log.info("Partitions revoked: {}", partitions)
        }

        override fun onPartitionsAssigned(consumer: Consumer<*, *>, partitions: Collection<TopicPartition>) {
            log.info("New partitions assigned: {}", partitions)
            partitions.forEach { partition ->
                val position = consumer.position(partition)
                log.info("  -> {}: offset={}", partition, position)
            }
        }

        override fun onPartitionsLost(consumer: Consumer<*, *>, partitions: Collection<TopicPartition>) {
            log.error("Partitions lost (unexpected revoke): {}", partitions)
        }
    }
}
