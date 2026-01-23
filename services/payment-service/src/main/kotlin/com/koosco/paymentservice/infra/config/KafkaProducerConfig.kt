package com.koosco.paymentservice.infra.config

import com.koosco.common.core.event.CloudEvent
import jakarta.annotation.PostConstruct
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.support.serializer.JsonSerializer

@Configuration
class KafkaProducerConfig(
    @Value("\${spring.kafka.bootstrap-servers}")
    private val bootstrapServers: String,
) {

    private val logger = LoggerFactory.getLogger(KafkaProducerConfig::class.java)

    @PostConstruct
    fun init() = logger.info("Kafka Producer - Bootstrap Servers: $bootstrapServers")

    @Bean
    @Primary
    fun producerFactory(): ProducerFactory<String, CloudEvent<*>> = DefaultKafkaProducerFactory(
        mapOf<String, Any>(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to JsonSerializer::class.java,
        ),
    )

    @Bean
    @Primary
    fun kafkaTemplate(): KafkaTemplate<String, CloudEvent<*>> = KafkaTemplate(producerFactory())
}
