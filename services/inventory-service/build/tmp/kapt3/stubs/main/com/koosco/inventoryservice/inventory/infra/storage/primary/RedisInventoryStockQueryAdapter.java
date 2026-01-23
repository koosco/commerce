package com.koosco.inventoryservice.inventory.infra.storage.primary;

/**
 * fileName       : RedisInventoryStockQueryAdapter
 * author         : koo
 * date           : 2025. 12. 29. 오전 4:55
 * description    : lua를 사용하지 않고 eventual consistency 허용
 */
@org.springframework.stereotype.Component()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0004\b\u0017\u0018\u00002\u00020\u0001B\u0019\u0012\u0012\u0010\u0002\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\u0002\u0010\u0005J\u0010\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u0004H\u0012J\u0010\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\u0004H\u0016J\u001c\u0010\f\u001a\b\u0012\u0004\u0012\u00020\n0\r2\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00040\rH\u0016J\u0010\u0010\u000f\u001a\u00020\u00042\u0006\u0010\u000b\u001a\u00020\u0004H\u0012J\u0010\u0010\u0010\u001a\u00020\u00042\u0006\u0010\u000b\u001a\u00020\u0004H\u0012R\u001a\u0010\u0002\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00040\u0003X\u0092\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0011"}, d2 = {"Lcom/koosco/inventoryservice/inventory/infra/storage/primary/RedisInventoryStockQueryAdapter;", "Lcom/koosco/inventoryservice/inventory/application/port/InventoryStockQueryPort;", "redisTemplate", "Lorg/springframework/data/redis/core/RedisTemplate;", "", "(Lorg/springframework/data/redis/core/RedisTemplate;)V", "getInt", "", "key", "getStock", "Lcom/koosco/inventoryservice/inventory/application/port/InventoryStockQueryPort$StockView;", "skuId", "getStocks", "", "skuIds", "reservedKey", "stockKey", "inventory-service"})
public class RedisInventoryStockQueryAdapter implements com.koosco.inventoryservice.inventory.application.port.InventoryStockQueryPort {
    @org.jetbrains.annotations.NotNull()
    private final org.springframework.data.redis.core.RedisTemplate<java.lang.String, java.lang.String> redisTemplate = null;
    
    public RedisInventoryStockQueryAdapter(@org.jetbrains.annotations.NotNull()
    org.springframework.data.redis.core.RedisTemplate<java.lang.String, java.lang.String> redisTemplate) {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public com.koosco.inventoryservice.inventory.application.port.InventoryStockQueryPort.StockView getStock(@org.jetbrains.annotations.NotNull()
    java.lang.String skuId) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.util.List<com.koosco.inventoryservice.inventory.application.port.InventoryStockQueryPort.StockView> getStocks(@org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> skuIds) {
        return null;
    }
    
    private int getInt(java.lang.String key) {
        return 0;
    }
    
    private java.lang.String stockKey(java.lang.String skuId) {
        return null;
    }
    
    private java.lang.String reservedKey(java.lang.String skuId) {
        return null;
    }
}