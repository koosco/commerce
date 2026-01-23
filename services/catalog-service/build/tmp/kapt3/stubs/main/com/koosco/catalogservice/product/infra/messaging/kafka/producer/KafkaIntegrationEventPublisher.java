package com.koosco.catalogservice.product.infra.messaging.kafka.producer;

/**
 * fileName       : KafkaIntegrationEventPublisher
 * author         : koo
 * date           : 2025. 12. 19. 오후 1:47
 * description    :
 */
@org.springframework.stereotype.Component()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0017\u0018\u00002\u00020\u0001B/\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0016\u0010\u0004\u001a\u0012\u0012\u0004\u0012\u00020\u0006\u0012\b\u0012\u0006\u0012\u0002\b\u00030\u00070\u0005\u0012\b\b\u0001\u0010\b\u001a\u00020\u0006\u00a2\u0006\u0002\u0010\tJ\u0010\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u0010H\u0016R\u001e\u0010\u0004\u001a\u0012\u0012\u0004\u0012\u00020\u0006\u0012\b\u0012\u0006\u0012\u0002\b\u00030\u00070\u0005X\u0092\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\n\u001a\n \f*\u0004\u0018\u00010\u000b0\u000bX\u0092\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0006X\u0092\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0092\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0011"}, d2 = {"Lcom/koosco/catalogservice/product/infra/messaging/kafka/producer/KafkaIntegrationEventPublisher;", "Lcom/koosco/catalogservice/product/application/port/IntegrationEventPublisher;", "topicResolver", "Lcom/koosco/catalogservice/common/config/KafkaTopicResolver;", "kafkaTemplate", "Lorg/springframework/kafka/core/KafkaTemplate;", "", "Lcom/koosco/common/core/event/CloudEvent;", "source", "(Lcom/koosco/catalogservice/common/config/KafkaTopicResolver;Lorg/springframework/kafka/core/KafkaTemplate;Ljava/lang/String;)V", "logger", "Lorg/slf4j/Logger;", "kotlin.jvm.PlatformType", "publish", "", "event", "Lcom/koosco/catalogservice/product/application/contract/ProductIntegrationEvent;", "catalog-service"})
public class KafkaIntegrationEventPublisher implements com.koosco.catalogservice.product.application.port.IntegrationEventPublisher {
    @org.jetbrains.annotations.NotNull()
    private final com.koosco.catalogservice.common.config.KafkaTopicResolver topicResolver = null;
    @org.jetbrains.annotations.NotNull()
    private final org.springframework.kafka.core.KafkaTemplate<java.lang.String, com.koosco.common.core.event.CloudEvent<?>> kafkaTemplate = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String source = null;
    private final org.slf4j.Logger logger = null;
    
    public KafkaIntegrationEventPublisher(@org.jetbrains.annotations.NotNull()
    com.koosco.catalogservice.common.config.KafkaTopicResolver topicResolver, @org.jetbrains.annotations.NotNull()
    org.springframework.kafka.core.KafkaTemplate<java.lang.String, com.koosco.common.core.event.CloudEvent<?>> kafkaTemplate, @org.springframework.beans.factory.annotation.Value(value = "${spring.application.name}")
    @org.jetbrains.annotations.NotNull()
    java.lang.String source) {
        super();
    }
    
    @java.lang.Override()
    public void publish(@org.jetbrains.annotations.NotNull()
    com.koosco.catalogservice.product.application.contract.ProductIntegrationEvent event) {
    }
}