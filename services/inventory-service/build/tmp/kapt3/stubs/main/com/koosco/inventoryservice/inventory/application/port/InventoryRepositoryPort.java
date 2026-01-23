package com.koosco.inventoryservice.inventory.application.port;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0004\bf\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&J\u001c\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u00072\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00050\u0007H&J\u001c\u0010\n\u001a\b\u0012\u0004\u0012\u00020\b0\u00072\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00050\u0007H&J\u0012\u0010\u000b\u001a\u0004\u0018\u00010\b2\u0006\u0010\u0004\u001a\u00020\u0005H&J\u0012\u0010\f\u001a\u0004\u0018\u00010\b2\u0006\u0010\u0004\u001a\u00020\u0005H&J\u0010\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\bH&J\u0016\u0010\u0010\u001a\u00020\u000e2\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\b0\u0007H&\u00a8\u0006\u0012"}, d2 = {"Lcom/koosco/inventoryservice/inventory/application/port/InventoryRepositoryPort;", "", "existsBySkuId", "", "skuId", "", "findAllBySkuIdIn", "", "Lcom/koosco/inventoryservice/inventory/domain/entity/Inventory;", "skuIds", "findAllBySkuIdInWithLock", "findBySkuIdOrNull", "findForUpdate", "save", "", "inventory", "saveAll", "inventories", "inventory-service"})
public abstract interface InventoryRepositoryPort {
    
    public abstract void save(@org.jetbrains.annotations.NotNull()
    com.koosco.inventoryservice.inventory.domain.entity.Inventory inventory);
    
    public abstract void saveAll(@org.jetbrains.annotations.NotNull()
    java.util.List<? extends com.koosco.inventoryservice.inventory.domain.entity.Inventory> inventories);
    
    @org.jetbrains.annotations.Nullable()
    public abstract com.koosco.inventoryservice.inventory.domain.entity.Inventory findBySkuIdOrNull(@org.jetbrains.annotations.NotNull()
    java.lang.String skuId);
    
    @org.jetbrains.annotations.Nullable()
    public abstract com.koosco.inventoryservice.inventory.domain.entity.Inventory findForUpdate(@org.jetbrains.annotations.NotNull()
    java.lang.String skuId);
    
    @org.jetbrains.annotations.NotNull()
    public abstract java.util.List<com.koosco.inventoryservice.inventory.domain.entity.Inventory> findAllBySkuIdIn(@org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> skuIds);
    
    /**
     * Pessimistic write lock을 획득하면서 재고 조회
     * 트랜잭션이 종료될 때까지 다른 트랜잭션의 읽기/쓰기 차단
     */
    @org.jetbrains.annotations.NotNull()
    public abstract java.util.List<com.koosco.inventoryservice.inventory.domain.entity.Inventory> findAllBySkuIdInWithLock(@org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> skuIds);
    
    public abstract boolean existsBySkuId(@org.jetbrains.annotations.NotNull()
    java.lang.String skuId);
}