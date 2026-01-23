package com.koosco.inventoryservice.inventory.infra.persist;

@org.springframework.stereotype.Repository()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0017\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u001c\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\t0\u0006H\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0092\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\n"}, d2 = {"Lcom/koosco/inventoryservice/inventory/infra/persist/InventoryQuery;", "", "queryFactory", "Lcom/querydsl/jpa/impl/JPAQueryFactory;", "(Lcom/querydsl/jpa/impl/JPAQueryFactory;)V", "findAllBySkuIdIn", "", "Lcom/koosco/inventoryservice/inventory/domain/entity/Inventory;", "ids", "", "inventory-service"})
public class InventoryQuery {
    @org.jetbrains.annotations.NotNull()
    private final com.querydsl.jpa.impl.JPAQueryFactory queryFactory = null;
    
    public InventoryQuery(@org.jetbrains.annotations.NotNull()
    com.querydsl.jpa.impl.JPAQueryFactory queryFactory) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public java.util.List<com.koosco.inventoryservice.inventory.domain.entity.Inventory> findAllBySkuIdIn(@org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> ids) {
        return null;
    }
}