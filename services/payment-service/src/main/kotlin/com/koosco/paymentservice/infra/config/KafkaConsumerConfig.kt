package com.koosco.paymentservice.infra.config

import com.koosco.common.core.event.CloudEvent
import jakarta.annotation.PostConstruct
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
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
import org.springframework.kafka.listener.RetryListener
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.util.backoff.ExponentialBackOff

@EnableKafka
@Configuration
class KafkaConsumerConfig(
    @Value("\${spring.kafka.bootstrap-servers}")
    private val bootstrapServers: String,
    @Value("\${spring.kafka.consumer.group-id:payment-service-group}")
    private val groupId: String,
    private val kafkaTemplate: KafkaTemplate<String, CloudEvent<*>>,
) {

    private val logger = LoggerFactory.getLogger(KafkaConsumerConfig::class.java)

    @PostConstruct
    fun init() {
        logger.info("Kafka Consumer - Bootstrap Servers: $bootstrapServers, Group ID: $groupId")
    }

    @Bean
    fun consumerFactory(): ConsumerFactory<String, CloudEvent<*>> {
        val props = mapOf<String, Any>(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
            ConsumerConfig.GROUP_ID_CONFIG to groupId,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to JsonDeserializer::class.java,
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest",
            JsonDeserializer.TRUSTED_PACKAGES to "*",
            JsonDeserializer.USE_TYPE_INFO_HEADERS to false,
            JsonDeserializer.VALUE_DEFAULT_TYPE to "com.koosco.common.core.event.CloudEvent",
        )
        return DefaultKafkaConsumerFactory(props)
    }

    @Bean
    fun deadLetterPublishingRecoverer(): DeadLetterPublishingRecoverer = DeadLetterPublishingRecoverer(kafkaTemplate)

    @Bean
    fun defaultErrorHandler(): DefaultErrorHandler {
        val backOff = ExponentialBackOff().apply {
            initialInterval = 1_000L
            multiplier = 2.0
            maxInterval = 10_000L
            maxElapsedTime = 30_000L
        }

        val errorHandler = DefaultErrorHandler(deadLetterPublishingRecoverer(), backOff)
        errorHandler.setRetryListeners(
            RetryListener { record, ex, deliveryAttempt ->
                logger.warn(
                    "Retry attempt {} for topic={}, partition={}, offset={}: {}",
                    deliveryAttempt,
                    record.topic(),
                    record.partition(),
                    record.offset(),
                    ex.message,
                )
            },
        )
        return errorHandler
    }

    @Bean
    fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, CloudEvent<*>> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, CloudEvent<*>>()
        factory.consumerFactory = consumerFactory()
        factory.containerProperties.ackMode =
            ContainerProperties.AckMode.MANUAL_IMMEDIATE
        factory.setCommonErrorHandler(defaultErrorHandler())
        return factory
    }
}
