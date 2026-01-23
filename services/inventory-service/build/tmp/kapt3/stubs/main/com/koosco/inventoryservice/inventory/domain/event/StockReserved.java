package com.koosco.inventoryservice.inventory.domain.event;

/**
 * 재고 예약 성공 이벤트
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u000e\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0005\b\u0086\b\u0018\u00002\u00020\u0001B)\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0007\u001a\u00020\b\u00a2\u0006\u0002\u0010\tJ\t\u0010\u0011\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0012\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0013\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0014\u001a\u00020\bH\u00c6\u0003J1\u0010\u0015\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\bH\u00c6\u0001J\u0013\u0010\u0016\u001a\u00020\u00172\b\u0010\u0018\u001a\u0004\u0018\u00010\u0019H\u00d6\u0003J\b\u0010\u001a\u001a\u00020\u0003H\u0016J\b\u0010\u001b\u001a\u00020\u0003H\u0016J\t\u0010\u001c\u001a\u00020\u0005H\u00d6\u0001J\t\u0010\u001d\u001a\u00020\u0003H\u00d6\u0001R\u0014\u0010\u0006\u001a\u00020\u0003X\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0014\u0010\u0007\u001a\u00020\bX\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u000b\u00a8\u0006\u001e"}, d2 = {"Lcom/koosco/inventoryservice/inventory/domain/event/StockReserved;", "Lcom/koosco/common/core/event/DomainEvent;", "skuId", "", "quantity", "", "eventId", "occurredAt", "Ljava/time/Instant;", "(Ljava/lang/String;ILjava/lang/String;Ljava/time/Instant;)V", "getEventId", "()Ljava/lang/String;", "getOccurredAt", "()Ljava/time/Instant;", "getQuantity", "()I", "getSkuId", "component1", "component2", "component3", "component4", "copy", "equals", "", "other", "", "getAggregateId", "getEventType", "hashCode", "toString", "inventory-service"})
public final class StockReserved implements com.koosco.common.core.event.DomainEvent {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String skuId = null;
    private final int quantity = 0;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String eventId = null;
    @org.jetbrains.annotations.NotNull()
    private final java.time.Instant occurredAt = null;
    
    public StockReserved(@org.jetbrains.annotations.NotNull()
    java.lang.String skuId, int quantity, @org.jetbrains.annotations.NotNull()
    java.lang.String eventId, @org.jetbrains.annotations.NotNull()
    java.time.Instant occurredAt) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getSkuId() {
        return null;
    }
    
    public final int getQuantity() {
        return 0;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String getEventId() {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.time.Instant getOccurredAt() {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String getAggregateId() {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String getEventType() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component1() {
        return null;
    }
    
    public final int component2() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.time.Instant component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.koosco.inventoryservice.inventory.domain.event.StockReserved copy(@org.jetbrains.annotations.NotNull()
    java.lang.String skuId, int quantity, @org.jetbrains.annotations.NotNull()
    java.lang.String eventId, @org.jetbrains.annotations.NotNull()
    java.time.Instant occurredAt) {
        return null;
    }
    
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String getEventVersion() {
        return null;
    }
    
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public com.koosco.common.core.event.CloudEvent<com.koosco.common.core.event.DomainEvent> toCloudEvent(@org.jetbrains.annotations.NotNull()
    java.lang.String source, @org.jetbrains.annotations.Nullable()
    java.lang.String dataSchema) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String toString() {
        return null;
    }
}