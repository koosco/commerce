package com.koosco.inventoryservice.inventory.infra.storage.secondary;

/**
 * fileName       : JpaInventorySnapshotWriter
 * author         : koo
 * date           : 2025. 12. 29. 오전 5:11
 * description    :
 */
@org.springframework.stereotype.Component()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0017\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J \u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\nH\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0092\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\f"}, d2 = {"Lcom/koosco/inventoryservice/inventory/infra/storage/secondary/JpaInventorySnapshotWriter;", "Lcom/koosco/inventoryservice/inventory/application/port/InventorySnapshotWriter;", "jpaRepository", "Lcom/koosco/inventoryservice/inventory/infra/storage/secondary/JpaInventorySnapshotRepository;", "(Lcom/koosco/inventoryservice/inventory/infra/storage/secondary/JpaInventorySnapshotRepository;)V", "write", "", "skuId", "", "total", "", "reserved", "inventory-service"})
public class JpaInventorySnapshotWriter implements com.koosco.inventoryservice.inventory.application.port.InventorySnapshotWriter {
    @org.jetbrains.annotations.NotNull()
    private final com.koosco.inventoryservice.inventory.infra.storage.secondary.JpaInventorySnapshotRepository jpaRepository = null;
    
    public JpaInventorySnapshotWriter(@org.jetbrains.annotations.NotNull()
    com.koosco.inventoryservice.inventory.infra.storage.secondary.JpaInventorySnapshotRepository jpaRepository) {
        super();
    }
    
    @java.lang.Override()
    public void write(@org.jetbrains.annotations.NotNull()
    java.lang.String skuId, int total, int reserved) {
    }
}