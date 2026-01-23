package com.koosco.inventoryservice.order;

/**
 * fileName       : TestOrderConsumer
 * author         : koo
 * date           : 2025. 12. 26. 오전 5:15
 * description    : Integration Event 발행 테스트를 위한 Consumer, local profile only
 */
@org.springframework.context.annotation.Profile(value = {"local"})
@org.springframework.stereotype.Component()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000J\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0010!\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0010$\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0017\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u0018\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\f2\u0006\u0010\u0011\u001a\u00020\u0001H\u0012J\b\u0010\u0012\u001a\u00020\u000fH\u0016J\u001a\u0010\u0013\u001a\u0014\u0012\u0004\u0012\u00020\f\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00010\u00150\u0014H\u0016J\u0016\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00010\u00152\u0006\u0010\u0010\u001a\u00020\fH\u0016J\u0014\u0010\u0017\u001a\u00020\u000f2\n\u0010\u0011\u001a\u0006\u0012\u0002\b\u00030\u0018H\u0017J\u0014\u0010\u0019\u001a\u00020\u000f2\n\u0010\u0011\u001a\u0006\u0012\u0002\b\u00030\u0018H\u0017J\u0014\u0010\u001a\u001a\u00020\u000f2\n\u0010\u0011\u001a\u0006\u0012\u0002\b\u00030\u0018H\u0017J\u0014\u0010\u001b\u001a\u00020\u000f2\n\u0010\u0011\u001a\u0006\u0012\u0002\b\u00030\u0018H\u0017R\u000e\u0010\u0004\u001a\u00020\u0005X\u0092\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0007\u001a\n \t*\u0004\u0018\u00010\b0\bX\u0092\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0092\u0004\u00a2\u0006\u0002\n\u0000R \u0010\n\u001a\u0014\u0012\u0004\u0012\u00020\f\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00010\r0\u000bX\u0092\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001c"}, d2 = {"Lcom/koosco/inventoryservice/order/TestOrderConsumer;", "", "objectMapper", "Lcom/fasterxml/jackson/databind/ObjectMapper;", "inventorySeedUseCase", "Lcom/koosco/inventoryservice/inventory/application/usecase/InventorySeedUseCase;", "(Lcom/fasterxml/jackson/databind/ObjectMapper;Lcom/koosco/inventoryservice/inventory/application/usecase/InventorySeedUseCase;)V", "logger", "Lorg/slf4j/Logger;", "kotlin.jvm.PlatformType", "receivedEvents", "Ljava/util/concurrent/ConcurrentHashMap;", "", "", "addEvent", "", "eventType", "event", "clearReceivedEvents", "getAllReceivedEvents", "", "", "getReceivedEvents", "onStockConfirmFailed", "Lcom/koosco/common/core/event/CloudEvent;", "onStockConfirmed", "onStockReservationFailed", "onStockReserved", "inventory-service"})
public class TestOrderConsumer {
    @org.jetbrains.annotations.NotNull()
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper = null;
    @org.jetbrains.annotations.NotNull()
    private final com.koosco.inventoryservice.inventory.application.usecase.InventorySeedUseCase inventorySeedUseCase = null;
    private final org.slf4j.Logger logger = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.concurrent.ConcurrentHashMap<java.lang.String, java.util.List<java.lang.Object>> receivedEvents = null;
    
    public TestOrderConsumer(@org.jetbrains.annotations.NotNull()
    com.fasterxml.jackson.databind.ObjectMapper objectMapper, @org.jetbrains.annotations.NotNull()
    com.koosco.inventoryservice.inventory.application.usecase.InventorySeedUseCase inventorySeedUseCase) {
        super();
    }
    
    @org.springframework.kafka.annotation.KafkaListener(topics = {"${inventory.topic.mappings.stock.reserved}"}, groupId = "inventory-service-test", containerFactory = "kafkaListenerContainerFactory")
    public void onStockReserved(@org.jetbrains.annotations.NotNull()
    com.koosco.common.core.event.CloudEvent<?> event) {
    }
    
    @org.springframework.kafka.annotation.KafkaListener(topics = {"${inventory.topic.mappings.stock.reservation.failed}"}, groupId = "inventory-service-test", containerFactory = "kafkaListenerContainerFactory")
    public void onStockReservationFailed(@org.jetbrains.annotations.NotNull()
    com.koosco.common.core.event.CloudEvent<?> event) {
    }
    
    @org.springframework.kafka.annotation.KafkaListener(topics = {"${inventory.topic.mappings.stock.confirmed}"}, groupId = "inventory-service-test", containerFactory = "kafkaListenerContainerFactory")
    public void onStockConfirmed(@org.jetbrains.annotations.NotNull()
    com.koosco.common.core.event.CloudEvent<?> event) {
    }
    
    @org.springframework.kafka.annotation.KafkaListener(topics = {"${inventory.topic.mappings.stock.confirm.failed}"}, groupId = "inventory-service-test", containerFactory = "kafkaListenerContainerFactory")
    public void onStockConfirmFailed(@org.jetbrains.annotations.NotNull()
    com.koosco.common.core.event.CloudEvent<?> event) {
    }
    
    private void addEvent(java.lang.String eventType, java.lang.Object event) {
    }
    
    /**
     * 수신한 이벤트 조회 (테스트 검증용)
     */
    @org.jetbrains.annotations.NotNull()
    public java.util.List<java.lang.Object> getReceivedEvents(@org.jetbrains.annotations.NotNull()
    java.lang.String eventType) {
        return null;
    }
    
    /**
     * 모든 수신 이벤트 조회
     */
    @org.jetbrains.annotations.NotNull()
    public java.util.Map<java.lang.String, java.util.List<java.lang.Object>> getAllReceivedEvents() {
        return null;
    }
    
    /**
     * 수신한 이벤트 초기화 (테스트 간 독립성 보장)
     */
    public void clearReceivedEvents() {
    }
}