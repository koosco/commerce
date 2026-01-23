package com.koosco.inventoryservice.inventory.infra.persist;

/**
 * fileName       : InventorySeedAdapter
 * author         : koo
 * date           : 2025. 12. 26. 오전 4:57
 * description    : 더미 데이터 영속성 처리를 위한 클래스, local profile only
 */
@org.springframework.context.annotation.Profile(value = {"local"})
@org.springframework.stereotype.Component()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010 \n\u0000\b\u0017\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\b\u0010\u0005\u001a\u00020\u0006H\u0017J\u0018\u0010\u0007\u001a\u00020\u00062\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bH\u0017J\u001e\u0010\f\u001a\u00020\u00062\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\t0\u000e2\u0006\u0010\n\u001a\u00020\u000bH\u0017R\u000e\u0010\u0002\u001a\u00020\u0003X\u0092\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000f"}, d2 = {"Lcom/koosco/inventoryservice/inventory/infra/persist/InventorySeedAdapter;", "Lcom/koosco/inventoryservice/inventory/application/port/InventorySeedPort;", "entityManager", "Ljakarta/persistence/EntityManager;", "(Ljakarta/persistence/EntityManager;)V", "clear", "", "initStock", "skuId", "", "initialQuantity", "", "initStocks", "skuIds", "", "inventory-service"})
public class InventorySeedAdapter implements com.koosco.inventoryservice.inventory.application.port.InventorySeedPort {
    @org.jetbrains.annotations.NotNull()
    private final jakarta.persistence.EntityManager entityManager = null;
    
    public InventorySeedAdapter(@org.jetbrains.annotations.NotNull()
    jakarta.persistence.EntityManager entityManager) {
        super();
    }
    
    @org.springframework.transaction.annotation.Transactional()
    @java.lang.Override()
    public void initStock(@org.jetbrains.annotations.NotNull()
    java.lang.String skuId, int initialQuantity) {
    }
    
    @org.springframework.transaction.annotation.Transactional()
    @java.lang.Override()
    public void initStocks(@org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> skuIds, int initialQuantity) {
    }
    
    @org.springframework.transaction.annotation.Transactional()
    @java.lang.Override()
    public void clear() {
    }
}