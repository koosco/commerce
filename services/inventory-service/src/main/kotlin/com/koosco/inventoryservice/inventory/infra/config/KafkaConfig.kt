package com.koosco.inventoryservice.inventory.infra.config

import com.koosco.common.core.event.CloudEvent
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
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.support.serializer.JsonSerializer

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
    fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, CloudEvent<*>> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, CloudEvent<*>>()
        factory.consumerFactory = consumerFactory()
        factory.containerProperties.ackMode =
            org.springframework.kafka.listener.ContainerProperties.AckMode.MANUAL_IMMEDIATE
        factory.containerProperties.setConsumerRebalanceListener(inventoryRebalanceListener())
        return factory
    }

    @Bean
    fun inventoryRebalanceListener(): ConsumerAwareRebalanceListener = object : ConsumerAwareRebalanceListener {
        override fun onPartitionsRevokedBeforeCommit(
            consumer: Consumer<*, *>,
            partitions: Collection<TopicPartition>,
        ) {
            log.warn("⚠️ Rebalance 시작 - 파티션 revoke 전: {}", partitions)
            try {
                consumer.commitSync()
                log.info("✅ Offset 커밋 완료 before revoke")
            } catch (e: Exception) {
                log.error("❌ Offset 커밋 실패: {}", e.message, e)
            }
        }

        override fun onPartitionsRevoked(partitions: Collection<TopicPartition>) {
            log.info("📉 파티션 revoke 완료: {}", partitions)
        }

        override fun onPartitionsAssigned(consumer: Consumer<*, *>, partitions: Collection<TopicPartition>) {
            log.info("📈 새 파티션 할당됨: {}", partitions)
            partitions.forEach { partition ->
                val position = consumer.position(partition)
                log.info("  → {}: offset={}", partition, position)
            }
        }

        override fun onPartitionsLost(consumer: Consumer<*, *>, partitions: Collection<TopicPartition>) {
            log.error("🚨 파티션 손실 (급작스러운 revoke): {}", partitions)
        }
    }
}
