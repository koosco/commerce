package com.koosco.inventoryservice.inventory.infra.config;

/**
 * fileName       : RedisConfig
 * author         : koo
 * date           : 2025. 12. 29. 오전 4:07
 * description    :
 */
@org.springframework.context.annotation.Configuration()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0017\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H\u0017J\u001c\u0010\u0005\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u00070\u00062\u0006\u0010\b\u001a\u00020\u0004H\u0017\u00a8\u0006\t"}, d2 = {"Lcom/koosco/inventoryservice/inventory/infra/config/RedisConfig;", "", "()V", "redisConnectionFactory", "Lorg/springframework/data/redis/connection/RedisConnectionFactory;", "redisTemplate", "Lorg/springframework/data/redis/core/RedisTemplate;", "", "connectionFactory", "inventory-service"})
public class RedisConfig {
    
    public RedisConfig() {
        super();
    }
    
    @org.springframework.context.annotation.Bean()
    @org.jetbrains.annotations.NotNull()
    public org.springframework.data.redis.connection.RedisConnectionFactory redisConnectionFactory() {
        return null;
    }
    
    @org.springframework.context.annotation.Bean()
    @org.jetbrains.annotations.NotNull()
    public org.springframework.data.redis.core.RedisTemplate<java.lang.String, java.lang.String> redisTemplate(@org.jetbrains.annotations.NotNull()
    org.springframework.data.redis.connection.RedisConnectionFactory connectionFactory) {
        return null;
    }
}