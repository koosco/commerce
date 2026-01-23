package com.koosco.catalogservice.common.infra.config;

@org.springframework.context.annotation.Configuration()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0017\u0018\u00002\u00020\u0001B\u000f\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\b\u0010\b\u001a\u00020\tH\u0017J\u0018\u0010\n\u001a\u0012\u0012\u0004\u0012\u00020\u0003\u0012\b\u0012\u0006\u0012\u0002\b\u00030\f0\u000bH\u0017J\u0018\u0010\r\u001a\u0012\u0012\u0004\u0012\u00020\u0003\u0012\b\u0012\u0006\u0012\u0002\b\u00030\f0\u000eH\u0017R\u000e\u0010\u0002\u001a\u00020\u0003X\u0092\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0005\u001a\n \u0007*\u0004\u0018\u00010\u00060\u0006X\u0092\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000f"}, d2 = {"Lcom/koosco/catalogservice/common/infra/config/KafkaProducerConfig;", "", "bootstrapServers", "", "(Ljava/lang/String;)V", "logger", "Lorg/slf4j/Logger;", "kotlin.jvm.PlatformType", "init", "", "kafkaTemplate", "Lorg/springframework/kafka/core/KafkaTemplate;", "Lcom/koosco/common/core/event/CloudEvent;", "producerFactory", "Lorg/springframework/kafka/core/ProducerFactory;", "catalog-service"})
public class KafkaProducerConfig {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String bootstrapServers = null;
    private final org.slf4j.Logger logger = null;
    
    public KafkaProducerConfig(@org.springframework.beans.factory.annotation.Value(value = "${spring.kafka.bootstrap-servers}")
    @org.jetbrains.annotations.NotNull()
    java.lang.String bootstrapServers) {
        super();
    }
    
    @jakarta.annotation.PostConstruct()
    public void init() {
    }
    
    @org.springframework.context.annotation.Bean()
    @org.springframework.context.annotation.Primary()
    @org.jetbrains.annotations.NotNull()
    public org.springframework.kafka.core.ProducerFactory<java.lang.String, com.koosco.common.core.event.CloudEvent<?>> producerFactory() {
        return null;
    }
    
    @org.springframework.context.annotation.Bean()
    @org.springframework.context.annotation.Primary()
    @org.jetbrains.annotations.NotNull()
    public org.springframework.kafka.core.KafkaTemplate<java.lang.String, com.koosco.common.core.event.CloudEvent<?>> kafkaTemplate() {
        return null;
    }
}