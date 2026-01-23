package com.koosco.inventoryservice.inventory.application.contract.outbound.inventory;

/**
 * 재고 확정 실패
 * OrderCompleted → Inventory.confirm 실패 시 발행
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u000f\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B+\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\u0007\u00a2\u0006\u0002\u0010\tJ\t\u0010\u0011\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010\u0012\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\t\u0010\u0013\u001a\u00020\u0007H\u00c6\u0003J\u000b\u0010\u0014\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J5\u0010\u0015\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\u0007H\u00c6\u0001J\u0013\u0010\u0016\u001a\u00020\u00172\b\u0010\u0018\u001a\u0004\u0018\u00010\u0019H\u00d6\u0003J\b\u0010\u001a\u001a\u00020\u0007H\u0016J\t\u0010\u001b\u001a\u00020\u001cH\u00d6\u0001J\t\u0010\u001d\u001a\u00020\u0007H\u00d6\u0001R\u0013\u0010\b\u001a\u0004\u0018\u00010\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\u000bR\u0014\u0010\u0002\u001a\u00020\u0003X\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010\u00a8\u0006\u001e"}, d2 = {"Lcom/koosco/inventoryservice/inventory/application/contract/outbound/inventory/StockConfirmFailedEvent;", "Lcom/koosco/inventoryservice/inventory/application/contract/InventoryIntegrationEvent;", "orderId", "", "reason", "Lcom/koosco/inventoryservice/inventory/domain/enums/StockConfirmFailReason;", "correlationId", "", "causationId", "(JLcom/koosco/inventoryservice/inventory/domain/enums/StockConfirmFailReason;Ljava/lang/String;Ljava/lang/String;)V", "getCausationId", "()Ljava/lang/String;", "getCorrelationId", "getOrderId", "()J", "getReason", "()Lcom/koosco/inventoryservice/inventory/domain/enums/StockConfirmFailReason;", "component1", "component2", "component3", "component4", "copy", "equals", "", "other", "", "getEventType", "hashCode", "", "toString", "inventory-service"})
public final class StockConfirmFailedEvent implements com.koosco.inventoryservice.inventory.application.contract.InventoryIntegrationEvent {
    private final long orderId = 0L;
    @org.jetbrains.annotations.Nullable()
    private final com.koosco.inventoryservice.inventory.domain.enums.StockConfirmFailReason reason = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String correlationId = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String causationId = null;
    
    public StockConfirmFailedEvent(long orderId, @org.jetbrains.annotations.Nullable()
    com.koosco.inventoryservice.inventory.domain.enums.StockConfirmFailReason reason, @org.jetbrains.annotations.NotNull()
    java.lang.String correlationId, @org.jetbrains.annotations.Nullable()
    java.lang.String causationId) {
        super();
    }
    
    @java.lang.Override()
    public long getOrderId() {
        return 0L;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.koosco.inventoryservice.inventory.domain.enums.StockConfirmFailReason getReason() {
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
    public final com.koosco.inventoryservice.inventory.domain.enums.StockConfirmFailReason component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component3() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.koosco.inventoryservice.inventory.application.contract.outbound.inventory.StockConfirmFailedEvent copy(long orderId, @org.jetbrains.annotations.Nullable()
    com.koosco.inventoryservice.inventory.domain.enums.StockConfirmFailReason reason, @org.jetbrains.annotations.NotNull()
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
}