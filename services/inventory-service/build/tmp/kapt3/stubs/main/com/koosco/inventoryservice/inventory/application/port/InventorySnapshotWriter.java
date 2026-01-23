package com.koosco.inventoryservice.inventory.application.port;

/**
 * fileName       : InventorySnapshotWriter
 * author         : koo
 * date           : 2025. 12. 29. 오전 5:08
 * description    :
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J \u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u0007H&\u00a8\u0006\t"}, d2 = {"Lcom/koosco/inventoryservice/inventory/application/port/InventorySnapshotWriter;", "", "write", "", "skuId", "", "total", "", "reserved", "inventory-service"})
public abstract interface InventorySnapshotWriter {
    
    public abstract void write(@org.jetbrains.annotations.NotNull()
    java.lang.String skuId, int total, int reserved);
}