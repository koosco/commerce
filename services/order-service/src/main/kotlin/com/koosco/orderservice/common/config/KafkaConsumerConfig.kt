package com.koosco.orderservice.common.config

import com.koosco.common.core.event.CloudEvent
import com.koosco.common.core.exception.BaseException
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
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.util.backoff.ExponentialBackOff

@EnableKafka
@Configuration
class KafkaConsumerConfig(
    @Value("\${spring.kafka.bootstrap-servers}")
    private val bootstrapServers: String,
    @Value("\${spring.kafka.consumer.group-id:order-service-group}")
    private val groupId: String,
) {

    private val logger = LoggerFactory.getLogger(KafkaConsumerConfig::class.java)

    @PostConstruct
    fun init() {
        logger.info("Kafka Consumer Configuration - Bootstrap Servers: $bootstrapServers, Group ID: $groupId")
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
    fun kafkaErrorHandler(kafkaTemplate: KafkaTemplate<*, *>): DefaultErrorHandler {
        val recoverer = DeadLetterPublishingRecoverer(kafkaTemplate)
        val backOff = ExponentialBackOff().apply {
            initialInterval = 1_000L
            multiplier = 2.0
            maxAttempts = 3
        }
        return DefaultErrorHandler(recoverer, backOff).also {
            it.addNotRetryableExceptions(BaseException::class.java)
            it.setCommitRecovered(true)
            logger.info("Kafka DLQ error handler configured: 3 retries with exponential backoff")
        }
    }

    @Bean
    fun kafkaListenerContainerFactory(
        kafkaErrorHandler: DefaultErrorHandler,
    ): ConcurrentKafkaListenerContainerFactory<String, CloudEvent<*>> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, CloudEvent<*>>()
        factory.consumerFactory = consumerFactory()
        factory.containerProperties.ackMode =
            ContainerProperties.AckMode.MANUAL_IMMEDIATE
        factory.setCommonErrorHandler(kafkaErrorHandler)
        return factory
    }
}
