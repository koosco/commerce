package com.koosco.inventoryservice.inventory.application.contract.inbound.catalog;

/**
 * fileName       : ProductSkuCreatedEvent
 * author         : koo
 * date           : 2025. 12. 24. 오전 2:34
 * description    :
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0002\b\u0004\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0015\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0086\b\u0018\u00002\u00020\u0001B?\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\u0006\u0010\u0007\u001a\u00020\u0005\u0012\u0006\u0010\b\u001a\u00020\u0003\u0012\b\b\u0002\u0010\t\u001a\u00020\n\u0012\u0006\u0010\u000b\u001a\u00020\f\u00a2\u0006\u0002\u0010\rJ\t\u0010\u0019\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001a\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u001b\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001c\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u001d\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001e\u001a\u00020\nH\u00c6\u0003J\t\u0010\u001f\u001a\u00020\fH\u00c6\u0003JO\u0010 \u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\u00052\b\b\u0002\u0010\b\u001a\u00020\u00032\b\b\u0002\u0010\t\u001a\u00020\n2\b\b\u0002\u0010\u000b\u001a\u00020\fH\u00c6\u0001J\u0013\u0010!\u001a\u00020\"2\b\u0010#\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010$\u001a\u00020\nH\u00d6\u0001J\t\u0010%\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u000b\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\t\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\b\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0011\u0010\u0007\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0011\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0013R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0015R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0013\u00a8\u0006&"}, d2 = {"Lcom/koosco/inventoryservice/inventory/application/contract/inbound/catalog/ProductSkuCreatedEvent;", "", "skuId", "", "productId", "", "productCode", "price", "optionValues", "initialQuantity", "", "createdAt", "Ljava/time/LocalDateTime;", "(Ljava/lang/String;JLjava/lang/String;JLjava/lang/String;ILjava/time/LocalDateTime;)V", "getCreatedAt", "()Ljava/time/LocalDateTime;", "getInitialQuantity", "()I", "getOptionValues", "()Ljava/lang/String;", "getPrice", "()J", "getProductCode", "getProductId", "getSkuId", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "copy", "equals", "", "other", "hashCode", "toString", "inventory-service"})
public final class ProductSkuCreatedEvent {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String skuId = null;
    private final long productId = 0L;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String productCode = null;
    private final long price = 0L;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String optionValues = null;
    private final int initialQuantity = 0;
    @org.jetbrains.annotations.NotNull()
    private final java.time.LocalDateTime createdAt = null;
    
    public ProductSkuCreatedEvent(@org.jetbrains.annotations.NotNull()
    java.lang.String skuId, long productId, @org.jetbrains.annotations.NotNull()
    java.lang.String productCode, long price, @org.jetbrains.annotations.NotNull()
    java.lang.String optionValues, int initialQuantity, @org.jetbrains.annotations.NotNull()
    java.time.LocalDateTime createdAt) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getSkuId() {
        return null;
    }
    
    public final long getProductId() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getProductCode() {
        return null;
    }
    
    public final long getPrice() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getOptionValues() {
        return null;
    }
    
    public final int getInitialQuantity() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.time.LocalDateTime getCreatedAt() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component1() {
        return null;
    }
    
    public final long component2() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component3() {
        return null;
    }
    
    public final long component4() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component5() {
        return null;
    }
    
    public final int component6() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.time.LocalDateTime component7() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.koosco.inventoryservice.inventory.application.contract.inbound.catalog.ProductSkuCreatedEvent copy(@org.jetbrains.annotations.NotNull()
    java.lang.String skuId, long productId, @org.jetbrains.annotations.NotNull()
    java.lang.String productCode, long price, @org.jetbrains.annotations.NotNull()
    java.lang.String optionValues, int initialQuantity, @org.jetbrains.annotations.NotNull()
    java.time.LocalDateTime createdAt) {
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