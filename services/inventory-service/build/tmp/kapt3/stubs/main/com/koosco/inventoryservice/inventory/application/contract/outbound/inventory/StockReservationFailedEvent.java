package com.koosco.inventoryservice.inventory.application.contract.outbound.inventory;

/**
 * 재고 예약 실패
 * OrderPlaced → Inventory.reserve 실패 시 발행
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0012\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\b\u0086\b\u0018\u00002\u00020\u0001:\u0001$B9\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\u000e\u0010\u0006\u001a\n\u0012\u0004\u0012\u00020\b\u0018\u00010\u0007\u0012\u0006\u0010\t\u001a\u00020\n\u0012\b\u0010\u000b\u001a\u0004\u0018\u00010\n\u00a2\u0006\u0002\u0010\fJ\t\u0010\u0016\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010\u0017\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\u0011\u0010\u0018\u001a\n\u0012\u0004\u0012\u00020\b\u0018\u00010\u0007H\u00c6\u0003J\t\u0010\u0019\u001a\u00020\nH\u00c6\u0003J\u000b\u0010\u001a\u001a\u0004\u0018\u00010\nH\u00c6\u0003JG\u0010\u001b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00052\u0010\b\u0002\u0010\u0006\u001a\n\u0012\u0004\u0012\u00020\b\u0018\u00010\u00072\b\b\u0002\u0010\t\u001a\u00020\n2\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\nH\u00c6\u0001J\u0013\u0010\u001c\u001a\u00020\u001d2\b\u0010\u001e\u001a\u0004\u0018\u00010\u001fH\u00d6\u0003J\b\u0010 \u001a\u00020\nH\u0016J\t\u0010!\u001a\u00020\"H\u00d6\u0001J\t\u0010#\u001a\u00020\nH\u00d6\u0001R\u0013\u0010\u000b\u001a\u0004\u0018\u00010\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\t\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u000eR\u0019\u0010\u0006\u001a\n\u0012\u0004\u0012\u00020\b\u0018\u00010\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0014\u0010\u0002\u001a\u00020\u0003X\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015\u00a8\u0006%"}, d2 = {"Lcom/koosco/inventoryservice/inventory/application/contract/outbound/inventory/StockReservationFailedEvent;", "Lcom/koosco/inventoryservice/inventory/application/contract/InventoryIntegrationEvent;", "orderId", "", "reason", "Lcom/koosco/inventoryservice/inventory/domain/enums/StockReservationFailReason;", "failedItems", "", "Lcom/koosco/inventoryservice/inventory/application/contract/outbound/inventory/StockReservationFailedEvent$FailedItem;", "correlationId", "", "causationId", "(JLcom/koosco/inventoryservice/inventory/domain/enums/StockReservationFailReason;Ljava/util/List;Ljava/lang/String;Ljava/lang/String;)V", "getCausationId", "()Ljava/lang/String;", "getCorrelationId", "getFailedItems", "()Ljava/util/List;", "getOrderId", "()J", "getReason", "()Lcom/koosco/inventoryservice/inventory/domain/enums/StockReservationFailReason;", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "", "other", "", "getEventType", "hashCode", "", "toString", "FailedItem", "inventory-service"})
public final class StockReservationFailedEvent implements com.koosco.inventoryservice.inventory.application.contract.InventoryIntegrationEvent {
    private final long orderId = 0L;
    @org.jetbrains.annotations.Nullable()
    private final com.koosco.inventoryservice.inventory.domain.enums.StockReservationFailReason reason = null;
    @org.jetbrains.annotations.Nullable()
    private final java.util.List<com.koosco.inventoryservice.inventory.application.contract.outbound.inventory.StockReservationFailedEvent.FailedItem> failedItems = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String correlationId = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String causationId = null;
    
    public StockReservationFailedEvent(long orderId, @org.jetbrains.annotations.Nullable()
    com.koosco.inventoryservice.inventory.domain.enums.StockReservationFailReason reason, @org.jetbrains.annotations.Nullable()
    java.util.List<com.koosco.inventoryservice.inventory.application.contract.outbound.inventory.StockReservationFailedEvent.FailedItem> failedItems, @org.jetbrains.annotations.NotNull()
    java.lang.String correlationId, @org.jetbrains.annotations.Nullable()
    java.lang.String causationId) {
        super();
    }
    
    @java.lang.Override()
    public long getOrderId() {
        return 0L;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.koosco.inventoryservice.inventory.domain.enums.StockReservationFailReason getReason() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.util.List<com.koosco.inventoryservice.inventory.application.contract.outbound.inventory.StockReservationFailedEvent.FailedItem> getFailedItems() {
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
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String getEventType() {
        return null;
    }
    
    public final long component1() {
        return 0L;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.koosco.inventoryservice.inventory.domain.enums.StockReservationFailReason component2() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.util.List<com.koosco.inventoryservice.inventory.application.contract.outbound.inventory.StockReservationFailedEvent.FailedItem> component3() {
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
    public final com.koosco.inventoryservice.inventory.application.contract.outbound.inventory.StockReservationFailedEvent copy(long orderId, @org.jetbrains.annotations.Nullable()
    com.koosco.inventoryservice.inventory.domain.enums.StockReservationFailReason reason, @org.jetbrains.annotations.Nullable()
    java.util.List<com.koosco.inventoryservice.inventory.application.contract.outbound.inventory.StockReservationFailedEvent.FailedItem> failedItems, @org.jetbrains.annotations.NotNull()
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
    @org.jetbrains.annotations.NotNull()
    public java.lang.String getPartitionKey() {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String getSubject() {
        return null;
    }
    
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public com.koosco.common.core.event.CloudEvent<java.lang.Object> toCloudEvent(@org.jetbrains.annotations.NotNull()
    java.lang.String source) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String toString() {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u000f\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0086\b\u0018\u00002\u00020\u0001B!\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\u0002\u0010\u0007J\t\u0010\u000f\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0010\u001a\u00020\u0005H\u00c6\u0003J\u0010\u0010\u0011\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003\u00a2\u0006\u0002\u0010\tJ.\u0010\u0012\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0005H\u00c6\u0001\u00a2\u0006\u0002\u0010\u0013J\u0013\u0010\u0014\u001a\u00020\u00152\b\u0010\u0016\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0017\u001a\u00020\u0005H\u00d6\u0001J\t\u0010\u0018\u001a\u00020\u0003H\u00d6\u0001R\u0015\u0010\u0006\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\n\n\u0002\u0010\n\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000e\u00a8\u0006\u0019"}, d2 = {"Lcom/koosco/inventoryservice/inventory/application/contract/outbound/inventory/StockReservationFailedEvent$FailedItem;", "", "skuId", "", "requestedQuantity", "", "availableQuantity", "(Ljava/lang/String;ILjava/lang/Integer;)V", "getAvailableQuantity", "()Ljava/lang/Integer;", "Ljava/lang/Integer;", "getRequestedQuantity", "()I", "getSkuId", "()Ljava/lang/String;", "component1", "component2", "component3", "copy", "(Ljava/lang/String;ILjava/lang/Integer;)Lcom/koosco/inventoryservice/inventory/application/contract/outbound/inventory/StockReservationFailedEvent$FailedItem;", "equals", "", "other", "hashCode", "toString", "inventory-service"})
    public static final class FailedItem {
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String skuId = null;
        private final int requestedQuantity = 0;
        @org.jetbrains.annotations.Nullable()
        private final java.lang.Integer availableQuantity = null;
        
        public FailedItem(@org.jetbrains.annotations.NotNull()
        java.lang.String skuId, int requestedQuantity, @org.jetbrains.annotations.Nullable()
        java.lang.Integer availableQuantity) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getSkuId() {
            return null;
        }
        
        public final int getRequestedQuantity() {
            return 0;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.Integer getAvailableQuantity() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component1() {
            return null;
        }
        
        public final int component2() {
            return 0;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.Integer component3() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.koosco.inventoryservice.inventory.application.contract.outbound.inventory.StockReservationFailedEvent.FailedItem copy(@org.jetbrains.annotations.NotNull()
        java.lang.String skuId, int requestedQuantity, @org.jetbrains.annotations.Nullable()
        java.lang.Integer availableQuantity) {
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