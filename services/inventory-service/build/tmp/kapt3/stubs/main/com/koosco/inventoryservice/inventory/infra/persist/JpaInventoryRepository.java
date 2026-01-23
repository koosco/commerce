package com.koosco.inventoryservice.inventory.infra.persist;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\b\u0004\bf\u0018\u00002\u000e\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u00030\u0001J\u001c\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00020\u00052\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00030\u0005H\'J\u0012\u0010\u0007\u001a\u0004\u0018\u00010\u00022\u0006\u0010\b\u001a\u00020\u0003H\'\u00a8\u0006\t"}, d2 = {"Lcom/koosco/inventoryservice/inventory/infra/persist/JpaInventoryRepository;", "Lorg/springframework/data/jpa/repository/JpaRepository;", "Lcom/koosco/inventoryservice/inventory/domain/entity/Inventory;", "", "findAllBySkuIdInWithLock", "", "skuIds", "findByIdWithLock", "skuId", "inventory-service"})
public abstract interface JpaInventoryRepository extends org.springframework.data.jpa.repository.JpaRepository<com.koosco.inventoryservice.inventory.domain.entity.Inventory, java.lang.String> {
    
    @org.springframework.data.jpa.repository.Lock(value = jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query(value = "select i from Inventory i where i.skuId = :skuId")
    @org.jetbrains.annotations.Nullable()
    public abstract com.koosco.inventoryservice.inventory.domain.entity.Inventory findByIdWithLock(@org.jetbrains.annotations.NotNull()
    java.lang.String skuId);
    
    /**
     * Pessimistic write lock을 사용하여 재고 조회
     * SELECT ... FOR UPDATE 쿼리 실행
     * 데드락 방지를 위해 skuId 순으로 정렬
     */
    @org.springframework.data.jpa.repository.Lock(value = jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query(value = "SELECT i FROM Inventory i WHERE i.skuId IN :skuIds ORDER BY i.skuId")
    @org.jetbrains.annotations.NotNull()
    public abstract java.util.List<com.koosco.inventoryservice.inventory.domain.entity.Inventory> findAllBySkuIdInWithLock(@org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> skuIds);
}