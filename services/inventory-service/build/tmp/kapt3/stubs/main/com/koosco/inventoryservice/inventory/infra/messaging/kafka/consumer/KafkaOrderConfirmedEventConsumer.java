package com.koosco.inventoryservice.inventory.infra.messaging.kafka.consumer;

/**
 * fileName       : KafkaOrderConfirmedEventConsumer
 * author         : koo
 * date           : 2025. 12. 19. 오후 2:27
 * description    :
 */
@org.springframework.stereotype.Component()
@org.springframework.validation.annotation.Validated()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0017\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u001e\u0010\b\u001a\u00020\t2\f\b\u0001\u0010\n\u001a\u0006\u0012\u0002\b\u00030\u000b2\u0006\u0010\f\u001a\u00020\rH\u0017R\u000e\u0010\u0002\u001a\u00020\u0003X\u0092\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0005\u001a\n \u0007*\u0004\u0018\u00010\u00060\u0006X\u0092\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000e"}, d2 = {"Lcom/koosco/inventoryservice/inventory/infra/messaging/kafka/consumer/KafkaOrderConfirmedEventConsumer;", "", "confirmStockUseCase", "Lcom/koosco/inventoryservice/inventory/application/usecase/ConfirmStockUseCase;", "(Lcom/koosco/inventoryservice/inventory/application/usecase/ConfirmStockUseCase;)V", "logger", "Lorg/slf4j/Logger;", "kotlin.jvm.PlatformType", "onOrderConfirmed", "", "event", "Lcom/koosco/common/core/event/CloudEvent;", "ack", "Lorg/springframework/kafka/support/Acknowledgment;", "inventory-service"})
public class KafkaOrderConfirmedEventConsumer {
    @org.jetbrains.annotations.NotNull()
    private final com.koosco.inventoryservice.inventory.application.usecase.ConfirmStockUseCase confirmStockUseCase = null;
    private final org.slf4j.Logger logger = null;
    
    public KafkaOrderConfirmedEventConsumer(@org.jetbrains.annotations.NotNull()
    com.koosco.inventoryservice.inventory.application.usecase.ConfirmStockUseCase confirmStockUseCase) {
        super();
    }
    
    @org.springframework.kafka.annotation.KafkaListener(topics = {"${inventory.topic.mappings.order.confirmed}"}, groupId = "${spring.kafka.consumer.group-id}")
    public void onOrderConfirmed(@jakarta.validation.Valid()
    @org.jetbrains.annotations.NotNull()
    com.koosco.common.core.event.CloudEvent<?> event, @org.jetbrains.annotations.NotNull()
    org.springframework.kafka.support.Acknowledgment ack) {
    }
}