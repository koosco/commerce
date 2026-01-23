package com.koosco.inventoryservice.inventory.application.port;

/**
 * fileName       : InventoryStorePort
 * author         : koo
 * date           : 2025. 12. 29. 오전 4:10
 * description    :
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\bf\u0018\u00002\u00020\u0001:\u0005\u0014\u0015\u0016\u0017\u0018J\u0016\u0010\u0002\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005H&J\u0016\u0010\u0007\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\b0\u0005H&J\u0016\u0010\t\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\n0\u0005H&J\u0016\u0010\u000b\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\f0\u0005H&J\u0018\u0010\r\u001a\u00020\u00032\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0011H&J\u0016\u0010\u0012\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00130\u0005H&\u00a8\u0006\u0019"}, d2 = {"Lcom/koosco/inventoryservice/inventory/application/port/InventoryStockStorePort;", "", "add", "", "items", "", "Lcom/koosco/inventoryservice/inventory/application/port/InventoryStockStorePort$AddItem;", "cancel", "Lcom/koosco/inventoryservice/inventory/application/port/InventoryStockStorePort$CancelItem;", "confirm", "Lcom/koosco/inventoryservice/inventory/application/port/InventoryStockStorePort$ConfirmItem;", "decrease", "Lcom/koosco/inventoryservice/inventory/application/port/InventoryStockStorePort$DecreaseItem;", "initialize", "skuId", "", "initialQuantity", "", "reserve", "Lcom/koosco/inventoryservice/inventory/application/port/InventoryStockStorePort$ReserveItem;", "AddItem", "CancelItem", "ConfirmItem", "DecreaseItem", "ReserveItem", "inventory-service"})
public abstract interface InventoryStockStorePort {
    
    public abstract void initialize(@org.jetbrains.annotations.NotNull()
    java.lang.String skuId, int initialQuantity);
    
    public abstract void add(@org.jetbrains.annotations.NotNull()
    java.util.List<com.koosco.inventoryservice.inventory.application.port.InventoryStockStorePort.AddItem> items);
    
    public abstract void reserve(@org.jetbrains.annotations.NotNull()
    java.util.List<com.koosco.inventoryservice.inventory.application.port.InventoryStockStorePort.ReserveItem> items);
    
    public abstract void confirm(@org.jetbrains.annotations.NotNull()
    java.util.List<com.koosco.inventoryservice.inventory.application.port.InventoryStockStorePort.ConfirmItem> items);
    
    public abstract void cancel(@org.jetbrains.annotations.NotNull()
    java.util.List<com.koosco.inventoryservice.inventory.application.port.InventoryStockStorePort.CancelItem> items);
    
    public abstract void decrease(@org.jetbrains.annotations.NotNull()
    java.util.List<com.koosco.inventoryservice.inventory.application.port.InventoryStockStorePort.DecreaseItem> items);
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\t\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0086\b\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\t\u0010\u000b\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\f\u001a\u00020\u0005H\u00c6\u0003J\u001d\u0010\r\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005H\u00c6\u0001J\u0013\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0011\u001a\u00020\u0005H\u00d6\u0001J\t\u0010\u0012\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\n\u00a8\u0006\u0013"}, d2 = {"Lcom/koosco/inventoryservice/inventory/application/port/InventoryStockStorePort$AddItem;", "", "skuId", "", "quantity", "", "(Ljava/lang/String;I)V", "getQuantity", "()I", "getSkuId", "()Ljava/lang/String;", "component1", "component2", "copy", "equals", "", "other", "hashCode", "toString", "inventory-service"})
    public static final class AddItem {
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String skuId = null;
        private final int quantity = 0;
        
        public AddItem(@org.jetbrains.annotations.NotNull()
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
        public final com.koosco.inventoryservice.inventory.application.port.InventoryStockStorePort.AddItem copy(@org.jetbrains.annotations.NotNull()
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
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\t\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0086\b\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\t\u0010\u000b\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\f\u001a\u00020\u0005H\u00c6\u0003J\u001d\u0010\r\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005H\u00c6\u0001J\u0013\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0011\u001a\u00020\u0005H\u00d6\u0001J\t\u0010\u0012\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\n\u00a8\u0006\u0013"}, d2 = {"Lcom/koosco/inventoryservice/inventory/application/port/InventoryStockStorePort$CancelItem;", "", "skuId", "", "quantity", "", "(Ljava/lang/String;I)V", "getQuantity", "()I", "getSkuId", "()Ljava/lang/String;", "component1", "component2", "copy", "equals", "", "other", "hashCode", "toString", "inventory-service"})
    public static final class CancelItem {
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String skuId = null;
        private final int quantity = 0;
        
        public CancelItem(@org.jetbrains.annotations.NotNull()
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
        public final com.koosco.inventoryservice.inventory.application.port.InventoryStockStorePort.CancelItem copy(@org.jetbrains.annotations.NotNull()
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
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\t\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0086\b\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\t\u0010\u000b\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\f\u001a\u00020\u0005H\u00c6\u0003J\u001d\u0010\r\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005H\u00c6\u0001J\u0013\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0011\u001a\u00020\u0005H\u00d6\u0001J\t\u0010\u0012\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\n\u00a8\u0006\u0013"}, d2 = {"Lcom/koosco/inventoryservice/inventory/application/port/InventoryStockStorePort$ConfirmItem;", "", "skuId", "", "quantity", "", "(Ljava/lang/String;I)V", "getQuantity", "()I", "getSkuId", "()Ljava/lang/String;", "component1", "component2", "copy", "equals", "", "other", "hashCode", "toString", "inventory-service"})
    public static final class ConfirmItem {
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String skuId = null;
        private final int quantity = 0;
        
        public ConfirmItem(@org.jetbrains.annotations.NotNull()
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
        public final com.koosco.inventoryservice.inventory.application.port.InventoryStockStorePort.ConfirmItem copy(@org.jetbrains.annotations.NotNull()
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
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\t\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0086\b\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\t\u0010\u000b\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\f\u001a\u00020\u0005H\u00c6\u0003J\u001d\u0010\r\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005H\u00c6\u0001J\u0013\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0011\u001a\u00020\u0005H\u00d6\u0001J\t\u0010\u0012\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\n\u00a8\u0006\u0013"}, d2 = {"Lcom/koosco/inventoryservice/inventory/application/port/InventoryStockStorePort$DecreaseItem;", "", "skuId", "", "quantity", "", "(Ljava/lang/String;I)V", "getQuantity", "()I", "getSkuId", "()Ljava/lang/String;", "component1", "component2", "copy", "equals", "", "other", "hashCode", "toString", "inventory-service"})
    public static final class DecreaseItem {
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String skuId = null;
        private final int quantity = 0;
        
        public DecreaseItem(@org.jetbrains.annotations.NotNull()
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
        public final com.koosco.inventoryservice.inventory.application.port.InventoryStockStorePort.DecreaseItem copy(@org.jetbrains.annotations.NotNull()
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
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\t\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0086\b\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\t\u0010\u000b\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\f\u001a\u00020\u0005H\u00c6\u0003J\u001d\u0010\r\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005H\u00c6\u0001J\u0013\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0011\u001a\u00020\u0005H\u00d6\u0001J\t\u0010\u0012\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\n\u00a8\u0006\u0013"}, d2 = {"Lcom/koosco/inventoryservice/inventory/application/port/InventoryStockStorePort$ReserveItem;", "", "skuId", "", "quantity", "", "(Ljava/lang/String;I)V", "getQuantity", "()I", "getSkuId", "()Ljava/lang/String;", "component1", "component2", "copy", "equals", "", "other", "hashCode", "toString", "inventory-service"})
    public static final class ReserveItem {
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String skuId = null;
        private final int quantity = 0;
        
        public ReserveItem(@org.jetbrains.annotations.NotNull()
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
        public final com.koosco.inventoryservice.inventory.application.port.InventoryStockStorePort.ReserveItem copy(@org.jetbrains.annotations.NotNull()
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