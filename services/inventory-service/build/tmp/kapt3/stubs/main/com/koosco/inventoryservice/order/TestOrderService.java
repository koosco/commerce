package com.koosco.inventoryservice.order;

/**
 * fileName       : TestOrderService
 * author         : koo
 * date           : 2025. 12. 25. 오전 2:14
 * description    : 주문 이벤트 발행 테스트를 위한 서비스 클래스, local 환경에서만 사용
 */
@org.springframework.context.annotation.Profile(value = {"local"})
@org.springframework.stereotype.Service()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0017\u0018\u0000 \u00102\u00020\u0001:\u0001\u0010B;\u0012\u0016\u0010\u0002\u001a\u0012\u0012\u0004\u0012\u00020\u0004\u0012\b\u0012\u0006\u0012\u0002\b\u00030\u00050\u0003\u0012\b\b\u0001\u0010\u0006\u001a\u00020\u0004\u0012\b\b\u0001\u0010\u0007\u001a\u00020\u0004\u0012\b\b\u0001\u0010\b\u001a\u00020\u0004\u00a2\u0006\u0002\u0010\tJ\b\u0010\n\u001a\u00020\u000bH\u0016J\b\u0010\f\u001a\u00020\rH\u0016J\b\u0010\u000e\u001a\u00020\u000fH\u0016R\u001e\u0010\u0002\u001a\u0012\u0012\u0004\u0012\u00020\u0004\u0012\b\u0012\u0006\u0012\u0002\b\u00030\u00050\u0003X\u0092\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0092\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0004X\u0092\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0092\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0011"}, d2 = {"Lcom/koosco/inventoryservice/order/TestOrderService;", "", "kafkaTemplate", "Lorg/springframework/kafka/core/KafkaTemplate;", "", "Lcom/koosco/common/core/event/CloudEvent;", "orderPlacedTopic", "orderCancelledTopic", "orderConfirmedTopic", "(Lorg/springframework/kafka/core/KafkaTemplate;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", "cancelOrder", "Lcom/koosco/inventoryservice/inventory/application/contract/inbound/order/OrderCancelledEvent;", "confirmOrder", "Lcom/koosco/inventoryservice/inventory/application/contract/inbound/order/OrderConfirmedEvent;", "placeOrder", "Lcom/koosco/inventoryservice/inventory/application/contract/inbound/order/OrderPlacedEvent;", "Companion", "inventory-service"})
public class TestOrderService {
    @org.jetbrains.annotations.NotNull()
    private final org.springframework.kafka.core.KafkaTemplate<java.lang.String, com.koosco.common.core.event.CloudEvent<?>> kafkaTemplate = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String orderPlacedTopic = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String orderCancelledTopic = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String orderConfirmedTopic = null;
    public static final long ORDER_ID = 11111111111L;
    public static final long USER_ID = 11111111111L;
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String FIRST_SKU_ID = "00001f4c-a36c-4a70-9347-413ce52d5d61";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String SECOND_SKU_ID = "0000298f-0c73-4df1-8576-ac232687c290";
    public static final int INITIAL_STOCK = 10000;
    @org.jetbrains.annotations.NotNull()
    public static final com.koosco.inventoryservice.order.TestOrderService.Companion Companion = null;
    
    public TestOrderService(@org.jetbrains.annotations.NotNull()
    org.springframework.kafka.core.KafkaTemplate<java.lang.String, com.koosco.common.core.event.CloudEvent<?>> kafkaTemplate, @org.springframework.beans.factory.annotation.Value(value = "${inventory.topic.mappings.order.placed}")
    @org.jetbrains.annotations.NotNull()
    java.lang.String orderPlacedTopic, @org.springframework.beans.factory.annotation.Value(value = "${inventory.topic.mappings.order.cancelled}")
    @org.jetbrains.annotations.NotNull()
    java.lang.String orderCancelledTopic, @org.springframework.beans.factory.annotation.Value(value = "${inventory.topic.mappings.order.confirmed}")
    @org.jetbrains.annotations.NotNull()
    java.lang.String orderConfirmedTopic) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public com.koosco.inventoryservice.inventory.application.contract.inbound.order.OrderPlacedEvent placeOrder() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public com.koosco.inventoryservice.inventory.application.contract.inbound.order.OrderCancelledEvent cancelOrder() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public com.koosco.inventoryservice.inventory.application.contract.inbound.order.OrderConfirmedEvent confirmOrder() {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\t\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\bX\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000b"}, d2 = {"Lcom/koosco/inventoryservice/order/TestOrderService$Companion;", "", "()V", "FIRST_SKU_ID", "", "INITIAL_STOCK", "", "ORDER_ID", "", "SECOND_SKU_ID", "USER_ID", "inventory-service"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}