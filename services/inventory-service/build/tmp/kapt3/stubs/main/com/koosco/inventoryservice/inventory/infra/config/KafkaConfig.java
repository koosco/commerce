package com.koosco.inventoryservice.inventory.infra.config;

@org.springframework.kafka.annotation.EnableKafka()
@org.springframework.context.annotation.Configuration()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0017\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0018\u0010\b\u001a\u0012\u0012\u0004\u0012\u00020\n\u0012\b\u0012\u0006\u0012\u0002\b\u00030\u000b0\tH\u0017J\b\u0010\f\u001a\u00020\rH\u0017J\u0018\u0010\u000e\u001a\u0012\u0012\u0004\u0012\u00020\n\u0012\b\u0012\u0006\u0012\u0002\b\u00030\u000b0\u000fH\u0017J\u0018\u0010\u0010\u001a\u0012\u0012\u0004\u0012\u00020\n\u0012\b\u0012\u0006\u0012\u0002\b\u00030\u000b0\u0011H\u0017J\u0018\u0010\u0012\u001a\u0012\u0012\u0004\u0012\u00020\n\u0012\b\u0012\u0006\u0012\u0002\b\u00030\u000b0\u0013H\u0017R\u000e\u0010\u0002\u001a\u00020\u0003X\u0092\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0005\u001a\n \u0007*\u0004\u0018\u00010\u00060\u0006X\u0092\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0014"}, d2 = {"Lcom/koosco/inventoryservice/inventory/infra/config/KafkaConfig;", "", "kafkaProperties", "Lorg/springframework/boot/autoconfigure/kafka/KafkaProperties;", "(Lorg/springframework/boot/autoconfigure/kafka/KafkaProperties;)V", "log", "Lorg/slf4j/Logger;", "kotlin.jvm.PlatformType", "consumerFactory", "Lorg/springframework/kafka/core/ConsumerFactory;", "", "Lcom/koosco/common/core/event/CloudEvent;", "inventoryRebalanceListener", "Lorg/springframework/kafka/listener/ConsumerAwareRebalanceListener;", "kafkaListenerContainerFactory", "Lorg/springframework/kafka/config/ConcurrentKafkaListenerContainerFactory;", "kafkaTemplate", "Lorg/springframework/kafka/core/KafkaTemplate;", "producerFactory", "Lorg/springframework/kafka/core/ProducerFactory;", "inventory-service"})
public class KafkaConfig {
    @org.jetbrains.annotations.NotNull()
    private final org.springframework.boot.autoconfigure.kafka.KafkaProperties kafkaProperties = null;
    private final org.slf4j.Logger log = null;
    
    public KafkaConfig(@org.jetbrains.annotations.NotNull()
    org.springframework.boot.autoconfigure.kafka.KafkaProperties kafkaProperties) {
        super();
    }
    
    @org.springframework.context.annotation.Bean()
    @org.jetbrains.annotations.NotNull()
    public org.springframework.kafka.core.ProducerFactory<java.lang.String, com.koosco.common.core.event.CloudEvent<?>> producerFactory() {
        return null;
    }
    
    @org.springframework.context.annotation.Bean()
    @org.jetbrains.annotations.NotNull()
    public org.springframework.kafka.core.KafkaTemplate<java.lang.String, com.koosco.common.core.event.CloudEvent<?>> kafkaTemplate() {
        return null;
    }
    
    @org.springframework.context.annotation.Bean()
    @org.jetbrains.annotations.NotNull()
    public org.springframework.kafka.core.ConsumerFactory<java.lang.String, com.koosco.common.core.event.CloudEvent<?>> consumerFactory() {
        return null;
    }
    
    @org.springframework.context.annotation.Bean()
    @org.jetbrains.annotations.NotNull()
    public org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory<java.lang.String, com.koosco.common.core.event.CloudEvent<?>> kafkaListenerContainerFactory() {
        return null;
    }
    
    @org.springframework.context.annotation.Bean()
    @org.jetbrains.annotations.NotNull()
    public org.springframework.kafka.listener.ConsumerAwareRebalanceListener inventoryRebalanceListener() {
        return null;
    }
}