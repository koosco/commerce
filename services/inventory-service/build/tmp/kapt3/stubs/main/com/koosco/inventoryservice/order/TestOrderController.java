package com.koosco.inventoryservice.order;

/**
 * fileName       : TestOrderController
 * author         : koo
 * date           : 2025. 12. 25. 오전 2:16
 * description    : 주문 이벤트 발행을 위한 테스트 컨트롤러, local 환경에서만 사용
 */
@org.springframework.context.annotation.Profile(value = {"local"})
@org.springframework.web.bind.annotation.RestController()
@org.springframework.web.bind.annotation.RequestMapping(value = {"/api/orders/test"})
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0017\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u000e\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bH\u0017J \u0010\n\u001a\u001a\u0012\u0016\u0012\u0014\u0012\u0004\u0012\u00020\f\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00010\r0\u000b0\bH\u0017J\u001e\u0010\u000e\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00010\r0\b2\b\b\u0001\u0010\u000f\u001a\u00020\fH\u0017J\u000e\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00110\bH\u0017J\u000e\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00130\bH\u0017J\u000e\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00150\bH\u0017R\u000e\u0010\u0004\u001a\u00020\u0005X\u0092\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0092\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0016"}, d2 = {"Lcom/koosco/inventoryservice/order/TestOrderController;", "", "testOrderService", "Lcom/koosco/inventoryservice/order/TestOrderService;", "testOrderConsumer", "Lcom/koosco/inventoryservice/order/TestOrderConsumer;", "(Lcom/koosco/inventoryservice/order/TestOrderService;Lcom/koosco/inventoryservice/order/TestOrderConsumer;)V", "clearReceivedEvents", "Lcom/koosco/common/core/response/ApiResponse;", "", "getAllReceivedEvents", "", "", "", "getReceivedEvents", "eventType", "orderCancelled", "Lcom/koosco/inventoryservice/inventory/application/contract/inbound/order/OrderCancelledEvent;", "orderConfirmed", "Lcom/koosco/inventoryservice/inventory/application/contract/inbound/order/OrderConfirmedEvent;", "orderPlaced", "Lcom/koosco/inventoryservice/inventory/application/contract/inbound/order/OrderPlacedEvent;", "inventory-service"})
public class TestOrderController {
    @org.jetbrains.annotations.NotNull()
    private final com.koosco.inventoryservice.order.TestOrderService testOrderService = null;
    @org.jetbrains.annotations.NotNull()
    private final com.koosco.inventoryservice.order.TestOrderConsumer testOrderConsumer = null;
    
    public TestOrderController(@org.jetbrains.annotations.NotNull()
    com.koosco.inventoryservice.order.TestOrderService testOrderService, @org.jetbrains.annotations.NotNull()
    com.koosco.inventoryservice.order.TestOrderConsumer testOrderConsumer) {
        super();
    }
    
    @org.springframework.web.bind.annotation.PostMapping(value = {"/placed"})
    @org.jetbrains.annotations.NotNull()
    public com.koosco.common.core.response.ApiResponse<com.koosco.inventoryservice.inventory.application.contract.inbound.order.OrderPlacedEvent> orderPlaced() {
        return null;
    }
    
    @org.springframework.web.bind.annotation.PostMapping(value = {"/cancelled"})
    @org.jetbrains.annotations.NotNull()
    public com.koosco.common.core.response.ApiResponse<com.koosco.inventoryservice.inventory.application.contract.inbound.order.OrderCancelledEvent> orderCancelled() {
        return null;
    }
    
    @org.springframework.web.bind.annotation.PostMapping(value = {"/confirmed"})
    @org.jetbrains.annotations.NotNull()
    public com.koosco.common.core.response.ApiResponse<com.koosco.inventoryservice.inventory.application.contract.inbound.order.OrderConfirmedEvent> orderConfirmed() {
        return null;
    }
    
    @org.springframework.web.bind.annotation.GetMapping(value = {"/events/{eventType}"})
    @org.jetbrains.annotations.NotNull()
    public com.koosco.common.core.response.ApiResponse<java.util.List<java.lang.Object>> getReceivedEvents(@org.springframework.web.bind.annotation.PathVariable()
    @org.jetbrains.annotations.NotNull()
    java.lang.String eventType) {
        return null;
    }
    
    @org.springframework.web.bind.annotation.GetMapping(value = {"/events"})
    @org.jetbrains.annotations.NotNull()
    public com.koosco.common.core.response.ApiResponse<java.util.Map<java.lang.String, java.util.List<java.lang.Object>>> getAllReceivedEvents() {
        return null;
    }
    
    @org.springframework.web.bind.annotation.DeleteMapping(value = {"/events"})
    @org.jetbrains.annotations.NotNull()
    public com.koosco.common.core.response.ApiResponse<kotlin.Unit> clearReceivedEvents() {
        return null;
    }
}