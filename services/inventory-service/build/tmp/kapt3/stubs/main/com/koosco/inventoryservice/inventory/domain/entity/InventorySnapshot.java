package com.koosco.inventoryservice.inventory.domain.entity;

/**
 * fileName       : InventorySnapshot
 * author         : koo
 * date           : 2025. 12. 29. 오전 5:15
 * description    :
 */
@jakarta.persistence.Entity()
@jakarta.persistence.Table(name = "inventory_snapshot")
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u000e\b\u0017\u0018\u00002\u00020\u0001B%\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\b\u00a2\u0006\u0002\u0010\tR\u001e\u0010\u0006\u001a\u00020\u00058\u0016@\u0016X\u0097\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\n\u0010\u000b\"\u0004\b\f\u0010\rR\u0016\u0010\u0002\u001a\u00020\u00038\u0016X\u0097\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u001e\u0010\u0007\u001a\u00020\b8\u0016@\u0016X\u0097\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0010\u0010\u0011\"\u0004\b\u0012\u0010\u0013R\u001e\u0010\u0004\u001a\u00020\u00058\u0016@\u0016X\u0097\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0014\u0010\u000b\"\u0004\b\u0015\u0010\r\u00a8\u0006\u0016"}, d2 = {"Lcom/koosco/inventoryservice/inventory/domain/entity/InventorySnapshot;", "", "skuId", "", "total", "", "reserved", "snapshottedAt", "Ljava/time/LocalDateTime;", "(Ljava/lang/String;IILjava/time/LocalDateTime;)V", "getReserved", "()I", "setReserved", "(I)V", "getSkuId", "()Ljava/lang/String;", "getSnapshottedAt", "()Ljava/time/LocalDateTime;", "setSnapshottedAt", "(Ljava/time/LocalDateTime;)V", "getTotal", "setTotal", "inventory-service"})
public class InventorySnapshot {
    @jakarta.persistence.Id()
    @jakarta.persistence.Column(name = "sku_id")
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String skuId = null;
    @jakarta.persistence.Column(nullable = false)
    private int total;
    @jakarta.persistence.Column(nullable = false)
    private int reserved;
    @jakarta.persistence.Column(nullable = false)
    @org.jetbrains.annotations.NotNull()
    private java.time.LocalDateTime snapshottedAt;
    
    public InventorySnapshot(@org.jetbrains.annotations.NotNull()
    java.lang.String skuId, int total, int reserved, @org.jetbrains.annotations.NotNull()
    java.time.LocalDateTime snapshottedAt) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public java.lang.String getSkuId() {
        return null;
    }
    
    public int getTotal() {
        return 0;
    }
    
    public void setTotal(int p0) {
    }
    
    public int getReserved() {
        return 0;
    }
    
    public void setReserved(int p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public java.time.LocalDateTime getSnapshottedAt() {
        return null;
    }
    
    public void setSnapshottedAt(@org.jetbrains.annotations.NotNull()
    java.time.LocalDateTime p0) {
    }
}