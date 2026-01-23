package com.koosco.inventoryservice.inventory.infra.config;

/**
 * fileName       : RedisScriptConfig
 * author         : koo
 * date           : 2025. 12. 29. 오전 4:12
 * description    :
 */
@org.springframework.context.annotation.Configuration()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0017\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004H\u0017J\u000e\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004H\u0017J\u000e\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004H\u0017J\u000e\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004H\u0017J\u0016\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00050\u00042\u0006\u0010\n\u001a\u00020\u000bH\u0012J\u000e\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004H\u0017\u00a8\u0006\r"}, d2 = {"Lcom/koosco/inventoryservice/inventory/infra/config/RedisScriptConfig;", "", "()V", "addStockScript", "Lorg/springframework/data/redis/core/script/DefaultRedisScript;", "", "cancelStockScript", "confirmStockScript", "decreaseStockScript", "load", "path", "", "reserveStockScript", "inventory-service"})
public class RedisScriptConfig {
    
    public RedisScriptConfig() {
        super();
    }
    
    @org.springframework.context.annotation.Bean()
    @org.jetbrains.annotations.NotNull()
    public org.springframework.data.redis.core.script.DefaultRedisScript<java.lang.Long> addStockScript() {
        return null;
    }
    
    @org.springframework.context.annotation.Bean()
    @org.jetbrains.annotations.NotNull()
    public org.springframework.data.redis.core.script.DefaultRedisScript<java.lang.Long> reserveStockScript() {
        return null;
    }
    
    @org.springframework.context.annotation.Bean()
    @org.jetbrains.annotations.NotNull()
    public org.springframework.data.redis.core.script.DefaultRedisScript<java.lang.Long> confirmStockScript() {
        return null;
    }
    
    @org.springframework.context.annotation.Bean()
    @org.jetbrains.annotations.NotNull()
    public org.springframework.data.redis.core.script.DefaultRedisScript<java.lang.Long> cancelStockScript() {
        return null;
    }
    
    @org.springframework.context.annotation.Bean()
    @org.jetbrains.annotations.NotNull()
    public org.springframework.data.redis.core.script.DefaultRedisScript<java.lang.Long> decreaseStockScript() {
        return null;
    }
    
    private org.springframework.data.redis.core.script.DefaultRedisScript<java.lang.Long> load(java.lang.String path) {
        return null;
    }
}