package com.koosco.inventoryservice.inventory.infra.storage.secondary;

/**
 * fileName       : JpaInventorySnapshotRepository
 * author         : koo
 * date           : 2025. 12. 29. 오후 11:56
 * description    :
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\bf\u0018\u00002\u000e\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u00030\u0001J \u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00032\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\bH\'\u00a8\u0006\n"}, d2 = {"Lcom/koosco/inventoryservice/inventory/infra/storage/secondary/JpaInventorySnapshotRepository;", "Lorg/springframework/data/jpa/repository/JpaRepository;", "Lcom/koosco/inventoryservice/inventory/domain/entity/InventorySnapshot;", "", "upsert", "", "skuId", "total", "", "reserved", "inventory-service"})
public abstract interface JpaInventorySnapshotRepository extends org.springframework.data.jpa.repository.JpaRepository<com.koosco.inventoryservice.inventory.domain.entity.InventorySnapshot, java.lang.String> {
    
    @org.springframework.data.jpa.repository.Modifying()
    @org.springframework.data.jpa.repository.Query(value = "\n        INSERT INTO inventory_snapshot(sku_id, total, reserved, snapshotted_at)\n        VALUES (:skuId, :total, :reserved, NOW())\n        ON DUPLICATE KEY UPDATE\n          total = :total,\n          reserved = :reserved,\n          snapshotted_at = NOW()\n    ", nativeQuery = true)
    public abstract void upsert(@org.jetbrains.annotations.NotNull()
    java.lang.String skuId, int total, int reserved);
}