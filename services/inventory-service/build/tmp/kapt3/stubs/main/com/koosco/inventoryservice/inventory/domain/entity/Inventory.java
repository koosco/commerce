package com.koosco.inventoryservice.inventory.domain.entity;

@jakarta.persistence.Entity()
@jakarta.persistence.Table(name = "inventory")
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\b\u0010\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0006\n\u0002\u0010 \n\u0002\b\u0003\b\u0017\u0018\u00002\u00020\u0001B;\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\u0007\u0012\u0010\b\u0002\u0010\t\u001a\n\u0012\u0004\u0012\u00020\u000b\u0018\u00010\n\u00a2\u0006\u0002\u0010\fJ\u0010\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u001eH\u0016J\u0010\u0010\u001f\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u001eH\u0016J\u0010\u0010 \u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u001eH\u0016J\u0010\u0010!\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u001eH\u0016J\b\u0010\"\u001a\u00020\u001cH\u0017J\b\u0010#\u001a\u00020\u001cH\u0017J\u000e\u0010$\u001a\b\u0012\u0004\u0012\u00020\u000b0%H\u0016J\u0010\u0010&\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u001eH\u0016J\u0010\u0010\'\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u001eH\u0016R\u001a\u0010\t\u001a\n\u0012\u0004\u0012\u00020\u000b\u0018\u00010\n8\u0012@\u0012X\u0093\u000e\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0006\u001a\u00020\u00078\u0016X\u0097\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u001a\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u000b0\n8RX\u0092\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0010\u0010\u0011R\u0016\u0010\u0002\u001a\u00020\u00038\u0016X\u0097\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u001e\u0010\u0004\u001a\u00020\u00058\u0016@\u0016X\u0097\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0014\u0010\u0015\"\u0004\b\u0016\u0010\u0017R\u001e\u0010\b\u001a\u00020\u00078\u0016@\u0016X\u0097\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0018\u0010\u000e\"\u0004\b\u0019\u0010\u001a\u00a8\u0006("}, d2 = {"Lcom/koosco/inventoryservice/inventory/domain/entity/Inventory;", "", "skuId", "", "stock", "Lcom/koosco/inventoryservice/inventory/domain/vo/Stock;", "createdAt", "Ljava/time/LocalDateTime;", "updatedAt", "_domainEvents", "", "Lcom/koosco/common/core/event/DomainEvent;", "(Ljava/lang/String;Lcom/koosco/inventoryservice/inventory/domain/vo/Stock;Ljava/time/LocalDateTime;Ljava/time/LocalDateTime;Ljava/util/List;)V", "getCreatedAt", "()Ljava/time/LocalDateTime;", "domainEvents", "getDomainEvents", "()Ljava/util/List;", "getSkuId", "()Ljava/lang/String;", "getStock", "()Lcom/koosco/inventoryservice/inventory/domain/vo/Stock;", "setStock", "(Lcom/koosco/inventoryservice/inventory/domain/vo/Stock;)V", "getUpdatedAt", "setUpdatedAt", "(Ljava/time/LocalDateTime;)V", "cancelReservation", "", "quantity", "", "confirm", "decrease", "increase", "onLoad", "onUpdate", "pullDomainEvents", "", "reserve", "updateStock", "inventory-service"})
public class Inventory {
    
    /**
     * Stock Keeping Unit ID
     */
    @jakarta.persistence.Id()
    @jakarta.persistence.Column(name = "sku_id", length = 50)
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String skuId = null;
    @jakarta.persistence.Embedded()
    @org.jetbrains.annotations.NotNull()
    private com.koosco.inventoryservice.inventory.domain.vo.Stock stock;
    @jakarta.persistence.Column(name = "created_at", nullable = false, updatable = false)
    @org.jetbrains.annotations.NotNull()
    private final java.time.LocalDateTime createdAt = null;
    @jakarta.persistence.Column(name = "updated_at", nullable = false)
    @org.jetbrains.annotations.NotNull()
    private java.time.LocalDateTime updatedAt;
    @jakarta.persistence.Transient()
    @org.jetbrains.annotations.Nullable()
    private java.util.List<com.koosco.common.core.event.DomainEvent> _domainEvents;
    
    public Inventory(@org.jetbrains.annotations.NotNull()
    java.lang.String skuId, @org.jetbrains.annotations.NotNull()
    com.koosco.inventoryservice.inventory.domain.vo.Stock stock, @org.jetbrains.annotations.NotNull()
    java.time.LocalDateTime createdAt, @org.jetbrains.annotations.NotNull()
    java.time.LocalDateTime updatedAt, @org.jetbrains.annotations.Nullable()
    java.util.List<com.koosco.common.core.event.DomainEvent> _domainEvents) {
        super();
    }
    
    /**
     * Stock Keeping Unit ID
     */
    @org.jetbrains.annotations.NotNull()
    public java.lang.String getSkuId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public com.koosco.inventoryservice.inventory.domain.vo.Stock getStock() {
        return null;
    }
    
    public void setStock(@org.jetbrains.annotations.NotNull()
    com.koosco.inventoryservice.inventory.domain.vo.Stock p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public java.time.LocalDateTime getCreatedAt() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public java.time.LocalDateTime getUpdatedAt() {
        return null;
    }
    
    public void setUpdatedAt(@org.jetbrains.annotations.NotNull()
    java.time.LocalDateTime p0) {
    }
    
    private java.util.List<com.koosco.common.core.event.DomainEvent> getDomainEvents() {
        return null;
    }
    
    @jakarta.persistence.PostLoad()
    public void onLoad() {
    }
    
    @jakarta.persistence.PreUpdate()
    public void onUpdate() {
    }
    
    @org.jetbrains.annotations.NotNull()
    public java.util.List<com.koosco.common.core.event.DomainEvent> pullDomainEvents() {
        return null;
    }
    
    public void updateStock(int quantity) {
    }
    
    /**
     * 재고 증가
     */
    public void increase(int quantity) {
    }
    
    /**
     * 재고 감소 (출고/폐기 등의 이유)
     */
    public void decrease(int quantity) {
    }
    
    /**
     * 재고 예약 (주문 생성 시)
     */
    public void reserve(int quantity) {
    }
    
    /**
     * 예약 재고 확정 (결제 성공 시)
     */
    public void confirm(int quantity) {
    }
    
    /**
     * 예약 취소 (결제 실패/주문 취소)
     */
    public void cancelReservation(int quantity) {
    }
}