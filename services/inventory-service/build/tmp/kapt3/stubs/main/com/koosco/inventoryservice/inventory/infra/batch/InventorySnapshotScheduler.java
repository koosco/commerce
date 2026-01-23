package com.koosco.inventoryservice.inventory.infra.batch;

/**
 * fileName       : InventorySnapshotScheduler
 * author         : koo
 * date           : 2025. 12. 29. 오전 5:09
 * description    :
 */
@org.springframework.stereotype.Component()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\b\u0017\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\b\u0010\u0007\u001a\u00020\bH\u0017R\u000e\u0010\u0002\u001a\u00020\u0003X\u0092\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0092\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\t"}, d2 = {"Lcom/koosco/inventoryservice/inventory/infra/batch/InventorySnapshotScheduler;", "", "snapshotQuery", "Lcom/koosco/inventoryservice/inventory/application/port/InventoryStockSnapshotQueryPort;", "snapshotWriter", "Lcom/koosco/inventoryservice/inventory/application/port/InventorySnapshotWriter;", "(Lcom/koosco/inventoryservice/inventory/application/port/InventoryStockSnapshotQueryPort;Lcom/koosco/inventoryservice/inventory/application/port/InventorySnapshotWriter;)V", "snapshot", "", "inventory-service"})
public class InventorySnapshotScheduler {
    @org.jetbrains.annotations.NotNull()
    private final com.koosco.inventoryservice.inventory.application.port.InventoryStockSnapshotQueryPort snapshotQuery = null;
    @org.jetbrains.annotations.NotNull()
    private final com.koosco.inventoryservice.inventory.application.port.InventorySnapshotWriter snapshotWriter = null;
    
    public InventorySnapshotScheduler(@org.jetbrains.annotations.NotNull()
    com.koosco.inventoryservice.inventory.application.port.InventoryStockSnapshotQueryPort snapshotQuery, @org.jetbrains.annotations.NotNull()
    com.koosco.inventoryservice.inventory.application.port.InventorySnapshotWriter snapshotWriter) {
        super();
    }
    
    @org.springframework.scheduling.annotation.Scheduled(fixedDelay = 60000L)
    public void snapshot() {
    }
}