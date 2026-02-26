package com.koosco.catalogservice.common.config

import com.koosco.common.core.event.CloudEvent
import com.koosco.common.core.exception.BaseException
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.StringDeserializer
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.listener.ContainerProperties
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer
import org.springframework.kafka.listener.DefaultErrorHandler
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.util.backoff.ExponentialBackOff

@EnableKafka
@Configuration
class KafkaConsumerConfig(
    private val kafkaProperties: KafkaProperties,
    private val kafkaTemplate: KafkaTemplate<String, CloudEvent<*>>,
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Bean
    fun consumerFactory(): ConsumerFactory<String, CloudEvent<*>> {
        val props = kafkaProperties.buildConsumerProperties(null).toMutableMap()

        props[ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG] =
            "org.apache.kafka.clients.consumer.CooperativeStickyAssignor"

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
    fun deadLetterPublishingRecoverer(): DeadLetterPublishingRecoverer =
        DeadLetterPublishingRecoverer(kafkaTemplate) { record, _ ->
            TopicPartition("${record.topic()}.DLT", record.partition())
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

    @Bean
    fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, CloudEvent<*>> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, CloudEvent<*>>()
        factory.consumerFactory = consumerFactory()
        factory.containerProperties.ackMode = ContainerProperties.AckMode.MANUAL_IMMEDIATE
        factory.setCommonErrorHandler(kafkaErrorHandler())
        return factory
    }

    companion object {
        private const val MAX_RETRY_ATTEMPTS = 3
    }
}
