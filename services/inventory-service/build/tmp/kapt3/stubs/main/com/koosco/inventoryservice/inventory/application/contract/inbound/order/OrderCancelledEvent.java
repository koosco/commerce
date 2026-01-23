package com.koosco.inventoryservice.inventory.application.contract.inbound.order;

/**
 * fileName       : StockReleaseRequestedEvent
 * author         : koo
 * date           : 2025. 12. 24. 오전 2:41
 * description    :
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0012\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\b\u0086\b\u0018\u00002\u00020\u0001:\u0001 B9\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007\u0012\u0006\u0010\t\u001a\u00020\u0005\u0012\b\u0010\n\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\u0002\u0010\u000bJ\t\u0010\u0014\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010\u0015\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\u000f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\b0\u0007H\u00c6\u0003J\t\u0010\u0017\u001a\u00020\u0005H\u00c6\u0003J\u000b\u0010\u0018\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003JE\u0010\u0019\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00052\u000e\b\u0002\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u00072\b\b\u0002\u0010\t\u001a\u00020\u00052\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u0005H\u00c6\u0001J\u0013\u0010\u001a\u001a\u00020\u001b2\b\u0010\u001c\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u001d\u001a\u00020\u001eH\u00d6\u0001J\t\u0010\u001f\u001a\u00020\u0005H\u00d6\u0001R\u0013\u0010\n\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\t\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\rR\u001c\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u00078\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\r\u00a8\u0006!"}, d2 = {"Lcom/koosco/inventoryservice/inventory/application/contract/inbound/order/OrderCancelledEvent;", "", "orderId", "", "reason", "", "items", "", "Lcom/koosco/inventoryservice/inventory/application/contract/inbound/order/OrderCancelledEvent$CancelledItem;", "correlationId", "causationId", "(JLjava/lang/String;Ljava/util/List;Ljava/lang/String;Ljava/lang/String;)V", "getCausationId", "()Ljava/lang/String;", "getCorrelationId", "getItems", "()Ljava/util/List;", "getOrderId", "()J", "getReason", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "", "other", "hashCode", "", "toString", "CancelledItem", "inventory-service"})
public final class OrderCancelledEvent {
    @jakarta.validation.constraints.NotNull()
    private final long orderId = 0L;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String reason = null;
    @jakarta.validation.constraints.NotEmpty()
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.koosco.inventoryservice.inventory.application.contract.inbound.order.OrderCancelledEvent.CancelledItem> items = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String correlationId = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String causationId = null;
    
    public OrderCancelledEvent(long orderId, @org.jetbrains.annotations.Nullable()
    java.lang.String reason, @org.jetbrains.annotations.NotNull()
    java.util.List<com.koosco.inventoryservice.inventory.application.contract.inbound.order.OrderCancelledEvent.CancelledItem> items, @org.jetbrains.annotations.NotNull()
    java.lang.String correlationId, @org.jetbrains.annotations.Nullable()
    java.lang.String causationId) {
        super();
    }
    
    public final long getOrderId() {
        return 0L;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getReason() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.koosco.inventoryservice.inventory.application.contract.inbound.order.OrderCancelledEvent.CancelledItem> getItems() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getCorrelationId() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getCausationId() {
        return null;
    }
    
    public final long component1() {
        return 0L;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.koosco.inventoryservice.inventory.application.contract.inbound.order.OrderCancelledEvent.CancelledItem> component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component4() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component5() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.koosco.inventoryservice.inventory.application.contract.inbound.order.OrderCancelledEvent copy(long orderId, @org.jetbrains.annotations.Nullable()
    java.lang.String reason, @org.jetbrains.annotations.NotNull()
    java.util.List<com.koosco.inventoryservice.inventory.application.contract.inbound.order.OrderCancelledEvent.CancelledItem> items, @org.jetbrains.annotations.NotNull()
    java.lang.String correlationId, @org.jetbrains.annotations.Nullable()
    java.lang.String causationId) {
        return null;
    }
    
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String toString() {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\t\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0086\b\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\t\u0010\u000b\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\f\u001a\u00020\u0005H\u00c6\u0003J\u001d\u0010\r\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005H\u00c6\u0001J\u0013\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0011\u001a\u00020\u0005H\u00d6\u0001J\t\u0010\u0012\u001a\u00020\u0003H\u00d6\u0001R\u0016\u0010\u0004\u001a\u00020\u00058\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\n\u00a8\u0006\u0013"}, d2 = {"Lcom/koosco/inventoryservice/inventory/application/contract/inbound/order/OrderCancelledEvent$CancelledItem;", "", "skuId", "", "quantity", "", "(Ljava/lang/String;I)V", "getQuantity", "()I", "getSkuId", "()Ljava/lang/String;", "component1", "component2", "copy", "equals", "", "other", "hashCode", "toString", "inventory-service"})
    public static final class CancelledItem {
        @jakarta.validation.constraints.NotNull()
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String skuId = null;
        @jakarta.validation.constraints.Positive()
        private final int quantity = 0;
        
        public CancelledItem(@org.jetbrains.annotations.NotNull()
        java.lang.String skuId, int quantity) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getSkuId() {
            return null;
        }
        
        public final int getQuantity() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component1() {
            return null;
        }
        
        public final int component2() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.koosco.inventoryservice.inventory.application.contract.inbound.order.OrderCancelledEvent.CancelledItem copy(@org.jetbrains.annotations.NotNull()
        java.lang.String skuId, int quantity) {
            return null;
        }
        
        @java.lang.Override()
        public boolean equals(@org.jetbrains.annotations.Nullable()
        java.lang.Object other) {
            return false;
        }
        
        @java.lang.Override()
        public int hashCode() {
            return 0;
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public java.lang.String toString() {
            return null;
        }
    }
}