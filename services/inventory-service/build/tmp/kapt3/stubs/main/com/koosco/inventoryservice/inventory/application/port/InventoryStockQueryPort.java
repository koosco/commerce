package com.koosco.inventoryservice.inventory.application.port;

/**
 * fileName       : InventoryStockQueryPort
 * author         : koo
 * date           : 2025. 12. 29. 오전 4:52
 * description    :
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\b\u0003\bf\u0018\u00002\u00020\u0001:\u0001\tJ\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&J\u001c\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00030\u00072\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00050\u0007H&\u00a8\u0006\n"}, d2 = {"Lcom/koosco/inventoryservice/inventory/application/port/InventoryStockQueryPort;", "", "getStock", "Lcom/koosco/inventoryservice/inventory/application/port/InventoryStockQueryPort$StockView;", "skuId", "", "getStocks", "", "skuIds", "StockView", "inventory-service"})
public abstract interface InventoryStockQueryPort {
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.koosco.inventoryservice.inventory.application.port.InventoryStockQueryPort.StockView getStock(@org.jetbrains.annotations.NotNull()
    java.lang.String skuId);
    
    @org.jetbrains.annotations.NotNull()
    public abstract java.util.List<com.koosco.inventoryservice.inventory.application.port.InventoryStockQueryPort.StockView> getStocks(@org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> skuIds);
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u000f\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0086\b\u0018\u00002\u00020\u0001B%\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\bJ\t\u0010\u000f\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0010\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0011\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0012\u001a\u00020\u0005H\u00c6\u0003J1\u0010\u0013\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u0007\u001a\u00020\u0005H\u00c6\u0001J\u0013\u0010\u0014\u001a\u00020\u00152\b\u0010\u0016\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0017\u001a\u00020\u0005H\u00d6\u0001J\t\u0010\u0018\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0007\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\nR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\n\u00a8\u0006\u0019"}, d2 = {"Lcom/koosco/inventoryservice/inventory/application/port/InventoryStockQueryPort$StockView;", "", "skuId", "", "total", "", "reserved", "available", "(Ljava/lang/String;III)V", "getAvailable", "()I", "getReserved", "getSkuId", "()Ljava/lang/String;", "getTotal", "component1", "component2", "component3", "component4", "copy", "equals", "", "other", "hashCode", "toString", "inventory-service"})
    public static final class StockView {
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String skuId = null;
        private final int total = 0;
        private final int reserved = 0;
        private final int available = 0;
        
        public StockView(@org.jetbrains.annotations.NotNull()
        java.lang.String skuId, int total, int reserved, int available) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getSkuId() {
            return null;
        }
        
        public final int getTotal() {
            return 0;
        }
        
        public final int getReserved() {
            return 0;
        }
        
        public final int getAvailable() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component1() {
            return null;
        }
        
        public final int component2() {
            return 0;
        }
        
        public final int component3() {
            return 0;
        }
        
        public final int component4() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.koosco.inventoryservice.inventory.application.port.InventoryStockQueryPort.StockView copy(@org.jetbrains.annotations.NotNull()
        java.lang.String skuId, int total, int reserved, int available) {
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