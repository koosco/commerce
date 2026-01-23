package com.koosco.inventoryservice.inventory.infra.storage.primary;

/**
 * fileName       : RedisInventoryStockAdapter
 * author         : koo
 * date           : 2025. 12. 29. 오전 4:12
 * description    :
 */
@org.springframework.stereotype.Component()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000T\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0017\u0018\u00002\u00020\u0001B_\u0012\u0012\u0010\u0002\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00040\u0003\u0012\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006\u0012\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006\u0012\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006\u0012\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006\u0012\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006\u00a2\u0006\u0002\u0010\fJ\u0016\u0010\r\u001a\u00020\u000e2\f\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00110\u0010H\u0016J\u0016\u0010\u0012\u001a\u00020\u000e2\f\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00130\u0010H\u0016J\u0016\u0010\u0014\u001a\u00020\u000e2\f\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00150\u0010H\u0016J\u0016\u0010\u0016\u001a\u00020\u000e2\f\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00170\u0010H\u0016J0\u0010\u0018\u001a\u00020\u00072\f\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\u0006\u0010\u001a\u001a\u00020\u00042\b\u0010\u001b\u001a\u0004\u0018\u00010\u00042\u0006\u0010\u001c\u001a\u00020\u001dH\u0012J\u0018\u0010\u001e\u001a\u00020\u000e2\u0006\u0010\u001f\u001a\u00020\u00042\u0006\u0010 \u001a\u00020\u001dH\u0016J\u0016\u0010!\u001a\u00020\u000e2\f\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\"0\u0010H\u0016J\u0010\u0010#\u001a\u00020\u00042\u0006\u0010\u001f\u001a\u00020\u0004H\u0012J\u0010\u0010$\u001a\u00020\u00042\u0006\u0010\u001f\u001a\u00020\u0004H\u0012R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0092\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0092\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0092\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0092\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u0002\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00040\u0003X\u0092\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0092\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006%"}, d2 = {"Lcom/koosco/inventoryservice/inventory/infra/storage/primary/RedisInventoryStockAdapter;", "Lcom/koosco/inventoryservice/inventory/application/port/InventoryStockStorePort;", "redisTemplate", "Lorg/springframework/data/redis/core/RedisTemplate;", "", "addStockScript", "Lorg/springframework/data/redis/core/script/DefaultRedisScript;", "", "reserveStockScript", "confirmStockScript", "cancelStockScript", "decreaseStockScript", "(Lorg/springframework/data/redis/core/RedisTemplate;Lorg/springframework/data/redis/core/script/DefaultRedisScript;Lorg/springframework/data/redis/core/script/DefaultRedisScript;Lorg/springframework/data/redis/core/script/DefaultRedisScript;Lorg/springframework/data/redis/core/script/DefaultRedisScript;Lorg/springframework/data/redis/core/script/DefaultRedisScript;)V", "add", "", "items", "", "Lcom/koosco/inventoryservice/inventory/application/port/InventoryStockStorePort$AddItem;", "cancel", "Lcom/koosco/inventoryservice/inventory/application/port/InventoryStockStorePort$CancelItem;", "confirm", "Lcom/koosco/inventoryservice/inventory/application/port/InventoryStockStorePort$ConfirmItem;", "decrease", "Lcom/koosco/inventoryservice/inventory/application/port/InventoryStockStorePort$DecreaseItem;", "exec", "script", "k1", "k2", "qty", "", "initialize", "skuId", "initialQuantity", "reserve", "Lcom/koosco/inventoryservice/inventory/application/port/InventoryStockStorePort$ReserveItem;", "reservedKey", "stockKey", "inventory-service"})
public class RedisInventoryStockAdapter implements com.koosco.inventoryservice.inventory.application.port.InventoryStockStorePort {
    @org.jetbrains.annotations.NotNull()
    private final org.springframework.data.redis.core.RedisTemplate<java.lang.String, java.lang.String> redisTemplate = null;
    @org.jetbrains.annotations.NotNull()
    private final org.springframework.data.redis.core.script.DefaultRedisScript<java.lang.Long> addStockScript = null;
    @org.jetbrains.annotations.NotNull()
    private final org.springframework.data.redis.core.script.DefaultRedisScript<java.lang.Long> reserveStockScript = null;
    @org.jetbrains.annotations.NotNull()
    private final org.springframework.data.redis.core.script.DefaultRedisScript<java.lang.Long> confirmStockScript = null;
    @org.jetbrains.annotations.NotNull()
    private final org.springframework.data.redis.core.script.DefaultRedisScript<java.lang.Long> cancelStockScript = null;
    @org.jetbrains.annotations.NotNull()
    private final org.springframework.data.redis.core.script.DefaultRedisScript<java.lang.Long> decreaseStockScript = null;
    
    public RedisInventoryStockAdapter(@org.jetbrains.annotations.NotNull()
    org.springframework.data.redis.core.RedisTemplate<java.lang.String, java.lang.String> redisTemplate, @org.jetbrains.annotations.NotNull()
    org.springframework.data.redis.core.script.DefaultRedisScript<java.lang.Long> addStockScript, @org.jetbrains.annotations.NotNull()
    org.springframework.data.redis.core.script.DefaultRedisScript<java.lang.Long> reserveStockScript, @org.jetbrains.annotations.NotNull()
    org.springframework.data.redis.core.script.DefaultRedisScript<java.lang.Long> confirmStockScript, @org.jetbrains.annotations.NotNull()
    org.springframework.data.redis.core.script.DefaultRedisScript<java.lang.Long> cancelStockScript, @org.jetbrains.annotations.NotNull()
    org.springframework.data.redis.core.script.DefaultRedisScript<java.lang.Long> decreaseStockScript) {
        super();
    }
    
    @java.lang.Override()
    public void initialize(@org.jetbrains.annotations.NotNull()
    java.lang.String skuId, int initialQuantity) {
    }
    
    @java.lang.Override()
    public void add(@org.jetbrains.annotations.NotNull()
    java.util.List<com.koosco.inventoryservice.inventory.application.port.InventoryStockStorePort.AddItem> items) {
    }
    
    @java.lang.Override()
    public void reserve(@org.jetbrains.annotations.NotNull()
    java.util.List<com.koosco.inventoryservice.inventory.application.port.InventoryStockStorePort.ReserveItem> items) {
    }
    
    @java.lang.Override()
    public void confirm(@org.jetbrains.annotations.NotNull()
    java.util.List<com.koosco.inventoryservice.inventory.application.port.InventoryStockStorePort.ConfirmItem> items) {
    }
    
    @java.lang.Override()
    public void cancel(@org.jetbrains.annotations.NotNull()
    java.util.List<com.koosco.inventoryservice.inventory.application.port.InventoryStockStorePort.CancelItem> items) {
    }
    
    @java.lang.Override()
    public void decrease(@org.jetbrains.annotations.NotNull()
    java.util.List<com.koosco.inventoryservice.inventory.application.port.InventoryStockStorePort.DecreaseItem> items) {
    }
    
    private long exec(org.springframework.data.redis.core.script.DefaultRedisScript<java.lang.Long> script, java.lang.String k1, java.lang.String k2, int qty) {
        return 0L;
    }
    
    private java.lang.String stockKey(java.lang.String skuId) {
        return null;
    }
    
    private java.lang.String reservedKey(java.lang.String skuId) {
        return null;
    }
}