package com.koosco.inventoryservice.inventory.infra.persist;

@org.springframework.stereotype.Repository()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0004\b\u0017\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u0010\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0016J\u001c\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\f2\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\n0\fH\u0016J\u001c\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\r0\f2\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\n0\fH\u0016J\u0012\u0010\u0010\u001a\u0004\u0018\u00010\r2\u0006\u0010\t\u001a\u00020\nH\u0016J\u0012\u0010\u0011\u001a\u0004\u0018\u00010\r2\u0006\u0010\t\u001a\u00020\nH\u0016J\u0010\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\rH\u0016J\u0016\u0010\u0015\u001a\u00020\u00132\f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\r0\fH\u0016R\u000e\u0010\u0004\u001a\u00020\u0005X\u0092\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0092\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0017"}, d2 = {"Lcom/koosco/inventoryservice/inventory/infra/persist/InventoryRepositoryImpl;", "Lcom/koosco/inventoryservice/inventory/application/port/InventoryRepositoryPort;", "jpaInventoryRepository", "Lcom/koosco/inventoryservice/inventory/infra/persist/JpaInventoryRepository;", "inventoryQuery", "Lcom/koosco/inventoryservice/inventory/infra/persist/InventoryQuery;", "(Lcom/koosco/inventoryservice/inventory/infra/persist/JpaInventoryRepository;Lcom/koosco/inventoryservice/inventory/infra/persist/InventoryQuery;)V", "existsBySkuId", "", "skuId", "", "findAllBySkuIdIn", "", "Lcom/koosco/inventoryservice/inventory/domain/entity/Inventory;", "skuIds", "findAllBySkuIdInWithLock", "findBySkuIdOrNull", "findForUpdate", "save", "", "inventory", "saveAll", "inventories", "inventory-service"})
public class InventoryRepositoryImpl implements com.koosco.inventoryservice.inventory.application.port.InventoryRepositoryPort {
    @org.jetbrains.annotations.NotNull()
    private final com.koosco.inventoryservice.inventory.infra.persist.JpaInventoryRepository jpaInventoryRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.koosco.inventoryservice.inventory.infra.persist.InventoryQuery inventoryQuery = null;
    
    public InventoryRepositoryImpl(@org.jetbrains.annotations.NotNull()
    com.koosco.inventoryservice.inventory.infra.persist.JpaInventoryRepository jpaInventoryRepository, @org.jetbrains.annotations.NotNull()
    com.koosco.inventoryservice.inventory.infra.persist.InventoryQuery inventoryQuery) {
        super();
    }
    
    @java.lang.Override()
    public void save(@org.jetbrains.annotations.NotNull()
    com.koosco.inventoryservice.inventory.domain.entity.Inventory inventory) {
    }
    
    @java.lang.Override()
    public void saveAll(@org.jetbrains.annotations.NotNull()
    java.util.List<? extends com.koosco.inventoryservice.inventory.domain.entity.Inventory> inventories) {
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public com.koosco.inventoryservice.inventory.domain.entity.Inventory findBySkuIdOrNull(@org.jetbrains.annotations.NotNull()
    java.lang.String skuId) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public com.koosco.inventoryservice.inventory.domain.entity.Inventory findForUpdate(@org.jetbrains.annotations.NotNull()
    java.lang.String skuId) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.util.List<com.koosco.inventoryservice.inventory.domain.entity.Inventory> findAllBySkuIdIn(@org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> skuIds) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.util.List<com.koosco.inventoryservice.inventory.domain.entity.Inventory> findAllBySkuIdInWithLock(@org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> skuIds) {
        return null;
    }
    
    @java.lang.Override()
    public boolean existsBySkuId(@org.jetbrains.annotations.NotNull()
    java.lang.String skuId) {
        return false;
    }
}