package com.koosco.inventoryservice.inventory.api.request;

/**
 * 대량 재고 감소 요청 DTO
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001:\u0001\u0011B\u0013\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\u0002\u0010\u0005J\u000f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00c6\u0003J\u0019\u0010\t\u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00c6\u0001J\u0013\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\r\u001a\u00020\u000eH\u00d6\u0001J\t\u0010\u000f\u001a\u00020\u0010H\u00d6\u0001R\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007\u00a8\u0006\u0012"}, d2 = {"Lcom/koosco/inventoryservice/inventory/api/request/BulkReduceStockRequest;", "", "items", "", "Lcom/koosco/inventoryservice/inventory/api/request/BulkReduceStockRequest$ReducingStockInfo;", "(Ljava/util/List;)V", "getItems", "()Ljava/util/List;", "component1", "copy", "equals", "", "other", "hashCode", "", "toString", "", "ReducingStockInfo", "inventory-service"})
public final class BulkReduceStockRequest {
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.koosco.inventoryservice.inventory.api.request.BulkReduceStockRequest.ReducingStockInfo> items = null;
    
    public BulkReduceStockRequest(@org.jetbrains.annotations.NotNull()
    java.util.List<com.koosco.inventoryservice.inventory.api.request.BulkReduceStockRequest.ReducingStockInfo> items) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.koosco.inventoryservice.inventory.api.request.BulkReduceStockRequest.ReducingStockInfo> getItems() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.koosco.inventoryservice.inventory.api.request.BulkReduceStockRequest.ReducingStockInfo> component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.koosco.inventoryservice.inventory.api.request.BulkReduceStockRequest copy(@org.jetbrains.annotations.NotNull()
    java.util.List<com.koosco.inventoryservice.inventory.api.request.BulkReduceStockRequest.ReducingStockInfo> items) {
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
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\t\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0086\b\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\t\u0010\u000b\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\f\u001a\u00020\u0005H\u00c6\u0003J\u001d\u0010\r\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005H\u00c6\u0001J\u0013\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0011\u001a\u00020\u0005H\u00d6\u0001J\t\u0010\u0012\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\n\u00a8\u0006\u0013"}, d2 = {"Lcom/koosco/inventoryservice/inventory/api/request/BulkReduceStockRequest$ReducingStockInfo;", "", "skuId", "", "quantity", "", "(Ljava/lang/String;I)V", "getQuantity", "()I", "getSkuId", "()Ljava/lang/String;", "component1", "component2", "copy", "equals", "", "other", "hashCode", "toString", "inventory-service"})
    public static final class ReducingStockInfo {
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String skuId = null;
        private final int quantity = 0;
        
        public ReducingStockInfo(@org.jetbrains.annotations.NotNull()
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
        public final com.koosco.inventoryservice.inventory.api.request.BulkReduceStockRequest.ReducingStockInfo copy(@org.jetbrains.annotations.NotNull()
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