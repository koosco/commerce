package com.koosco.inventoryservice.base;

/**
 * fileName       : RedisContainerTestBase
 * author         : koo
 * date           : 2025. 12. 30. 오전 12:28
 * description    : Redis Testcontainers를 사용하는 테스트의 베이스 클래스
 *                 - Redis 컨테이너를 자동으로 시작/종료
 *                 - Spring Boot의 Redis 설정을 동적으로 구성
 *                 - 모든 Redis 관련 통합 테스트에서 상속하여 사용
 */
@org.testcontainers.junit.jupiter.Testcontainers()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\b\'\u0018\u0000 \u00032\u00020\u0001:\u0001\u0003B\u0005\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0004"}, d2 = {"Lcom/koosco/inventoryservice/base/RedisContainerTestBase;", "", "()V", "Companion", "inventory-service_test"})
public abstract class RedisContainerTestBase {
    @org.testcontainers.junit.jupiter.Container()
    @org.jetbrains.annotations.NotNull()
    private static final org.testcontainers.containers.GenericContainer<?> redis = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.koosco.inventoryservice.base.RedisContainerTestBase.Companion Companion = null;
    
    public RedisContainerTestBase() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public static final org.testcontainers.containers.GenericContainer<?> getRedis() {
        return null;
    }
    
    @org.springframework.test.context.DynamicPropertySource()
    @kotlin.jvm.JvmStatic()
    public static final void redisProperties(@org.jetbrains.annotations.NotNull()
    org.springframework.test.context.DynamicPropertyRegistry registry) {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bH\u0007R \u0010\u0003\u001a\u0006\u0012\u0002\b\u00030\u00048\u0006X\u0087\u0004\u00a2\u0006\u000e\n\u0000\u0012\u0004\b\u0005\u0010\u0002\u001a\u0004\b\u0006\u0010\u0007\u00a8\u0006\f"}, d2 = {"Lcom/koosco/inventoryservice/base/RedisContainerTestBase$Companion;", "", "()V", "redis", "Lorg/testcontainers/containers/GenericContainer;", "getRedis$annotations", "getRedis", "()Lorg/testcontainers/containers/GenericContainer;", "redisProperties", "", "registry", "Lorg/springframework/test/context/DynamicPropertyRegistry;", "inventory-service_test"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final org.testcontainers.containers.GenericContainer<?> getRedis() {
            return null;
        }
        
        @kotlin.jvm.JvmStatic()
        @java.lang.Deprecated()
        public static void getRedis$annotations() {
        }
        
        @org.springframework.test.context.DynamicPropertySource()
        @kotlin.jvm.JvmStatic()
        public final void redisProperties(@org.jetbrains.annotations.NotNull()
        org.springframework.test.context.DynamicPropertyRegistry registry) {
        }
    }
}