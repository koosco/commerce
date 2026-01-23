package com.koosco.inventoryservice.common.config.kafka;

/**
 * fileName       : TopicResolver
 * author         : koo
 * date           : 2025. 12. 19. 오후 1:24
 * description    : domain event와 topic mapping
 */
@org.springframework.stereotype.Component()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0017\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0010\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0092\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\t"}, d2 = {"Lcom/koosco/inventoryservice/common/config/kafka/KafkaTopicResolver;", "Lcom/koosco/inventoryservice/inventory/infra/messaging/IntegrationTopicResolver;", "props", "Lcom/koosco/inventoryservice/common/config/kafka/KafkaTopicProperties;", "(Lcom/koosco/inventoryservice/common/config/kafka/KafkaTopicProperties;)V", "resolve", "", "event", "Lcom/koosco/inventoryservice/inventory/application/contract/InventoryIntegrationEvent;", "inventory-service"})
public class KafkaTopicResolver implements com.koosco.inventoryservice.inventory.infra.messaging.IntegrationTopicResolver {
    @org.jetbrains.annotations.NotNull()
    private final com.koosco.inventoryservice.common.config.kafka.KafkaTopicProperties props = null;
    
    public KafkaTopicResolver(@org.jetbrains.annotations.NotNull()
    com.koosco.inventoryservice.common.config.kafka.KafkaTopicProperties props) {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String resolve(@org.jetbrains.annotations.NotNull()
    com.koosco.inventoryservice.inventory.application.contract.InventoryIntegrationEvent event) {
        return null;
    }
}