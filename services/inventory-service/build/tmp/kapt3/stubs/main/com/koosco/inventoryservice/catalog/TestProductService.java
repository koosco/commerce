package com.koosco.inventoryservice.catalog;

/**
 * fileName       : TestProductService
 * author         : koo
 * date           : 2025. 12. 27. 오후 6:52
 * description    : Integration Event 발행 테스트를 위한 Service, local profile only
 */
@org.springframework.context.annotation.Profile(value = {"local"})
@org.springframework.stereotype.Service()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0017\u0018\u0000 \u000b2\u00020\u0001:\u0001\u000bB\'\u0012\u0016\u0010\u0002\u001a\u0012\u0012\u0004\u0012\u00020\u0004\u0012\b\u0012\u0006\u0012\u0002\b\u00030\u00050\u0003\u0012\b\b\u0001\u0010\u0006\u001a\u00020\u0004\u00a2\u0006\u0002\u0010\u0007J\u000e\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\tH\u0017R\u001e\u0010\u0002\u001a\u0012\u0012\u0004\u0012\u00020\u0004\u0012\b\u0012\u0006\u0012\u0002\b\u00030\u00050\u0003X\u0092\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0092\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\f"}, d2 = {"Lcom/koosco/inventoryservice/catalog/TestProductService;", "", "kafkaTemplate", "Lorg/springframework/kafka/core/KafkaTemplate;", "", "Lcom/koosco/common/core/event/CloudEvent;", "productCreatedTopic", "(Lorg/springframework/kafka/core/KafkaTemplate;Ljava/lang/String;)V", "execute", "", "Lcom/koosco/inventoryservice/inventory/application/contract/inbound/catalog/ProductSkuCreatedEvent;", "Companion", "inventory-service"})
public class TestProductService {
    @org.jetbrains.annotations.NotNull()
    private final org.springframework.kafka.core.KafkaTemplate<java.lang.String, com.koosco.common.core.event.CloudEvent<?>> kafkaTemplate = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String productCreatedTopic = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.List<com.koosco.inventoryservice.catalog.TestProduct> PRODUCTS = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.koosco.inventoryservice.catalog.TestProductService.Companion Companion = null;
    
    public TestProductService(@org.jetbrains.annotations.NotNull()
    org.springframework.kafka.core.KafkaTemplate<java.lang.String, com.koosco.common.core.event.CloudEvent<?>> kafkaTemplate, @org.springframework.beans.factory.annotation.Value(value = "${inventory.topic.mappings.product.sku.created}")
    @org.jetbrains.annotations.NotNull()
    java.lang.String productCreatedTopic) {
        super();
    }
    
    @org.springframework.transaction.annotation.Transactional()
    @org.jetbrains.annotations.NotNull()
    public java.util.List<com.koosco.inventoryservice.inventory.application.contract.inbound.catalog.ProductSkuCreatedEvent> execute() {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u0017\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007\u00a8\u0006\b"}, d2 = {"Lcom/koosco/inventoryservice/catalog/TestProductService$Companion;", "", "()V", "PRODUCTS", "", "Lcom/koosco/inventoryservice/catalog/TestProduct;", "getPRODUCTS", "()Ljava/util/List;", "inventory-service"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<com.koosco.inventoryservice.catalog.TestProduct> getPRODUCTS() {
            return null;
        }
    }
}