package com.koosco.inventoryservice.inventory.domain.vo;

@jakarta.persistence.Embeddable()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u000f\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0000\b\u0097\b\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0005J\u0010\u0010\u000b\u001a\u00020\u00002\u0006\u0010\f\u001a\u00020\u0003H\u0016J\t\u0010\r\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u000e\u001a\u00020\u0003H\u00c6\u0003J\u0010\u0010\u000f\u001a\u00020\u00002\u0006\u0010\f\u001a\u00020\u0003H\u0016J\u001d\u0010\u0010\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0003H\u00c6\u0001J\u0010\u0010\u0011\u001a\u00020\u00002\u0006\u0010\f\u001a\u00020\u0003H\u0016J\u0013\u0010\u0012\u001a\u00020\u00132\b\u0010\u0014\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0015\u001a\u00020\u0003H\u00d6\u0001J\u0010\u0010\u0016\u001a\u00020\u00002\u0006\u0010\f\u001a\u00020\u0003H\u0016J\u0010\u0010\u0017\u001a\u00020\u00002\u0006\u0010\f\u001a\u00020\u0003H\u0016J\t\u0010\u0018\u001a\u00020\u0019H\u00d6\u0001R\u0014\u0010\u0006\u001a\u00020\u00038VX\u0096\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0007\u0010\bR\u0016\u0010\u0004\u001a\u00020\u00038\u0016X\u0097\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\bR\u0016\u0010\u0002\u001a\u00020\u00038\u0016X\u0097\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\b\u00a8\u0006\u001a"}, d2 = {"Lcom/koosco/inventoryservice/inventory/domain/vo/Stock;", "", "total", "", "reserved", "(II)V", "available", "getAvailable", "()I", "getReserved", "getTotal", "cancelReservation", "q", "component1", "component2", "confirm", "copy", "decrease", "equals", "", "other", "hashCode", "increase", "reserve", "toString", "", "inventory-service"})
public class Stock {
    @jakarta.persistence.Column(name = "total_stock", nullable = false)
    private final int total = 0;
    @jakarta.persistence.Column(name = "reserved_stock", nullable = false)
    private final int reserved = 0;
    
    public Stock(int total, int reserved) {
        super();
    }
    
    public int getTotal() {
        return 0;
    }
    
    public int getReserved() {
        return 0;
    }
    
    public int getAvailable() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public com.koosco.inventoryservice.inventory.domain.vo.Stock increase(int q) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public com.koosco.inventoryservice.inventory.domain.vo.Stock decrease(int q) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public com.koosco.inventoryservice.inventory.domain.vo.Stock reserve(int q) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public com.koosco.inventoryservice.inventory.domain.vo.Stock confirm(int q) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public com.koosco.inventoryservice.inventory.domain.vo.Stock cancelReservation(int q) {
        return null;
    }
    
    public final int component1() {
        return 0;
    }
    
    public final int component2() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.koosco.inventoryservice.inventory.domain.vo.Stock copy(int total, int reserved) {
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