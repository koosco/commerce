package com.koosco.inventoryservice.base;

/**
 * RedisContainerTestBase 사용 예제 및 검증 테스트
 */
@org.springframework.boot.test.context.SpringBootTest()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\b\u0017\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0005\u001a\u00020\u0006H\u0017J\b\u0010\u0007\u001a\u00020\u0006H\u0017J\b\u0010\b\u001a\u00020\u0006H\u0017J\b\u0010\t\u001a\u00020\u0006H\u0017R\u0012\u0010\u0003\u001a\u00020\u00048\u0012@\u0012X\u0093.\u00a2\u0006\u0002\n\u0000\u00a8\u0006\n"}, d2 = {"Lcom/koosco/inventoryservice/base/RedisContainerTestBaseTest;", "Lcom/koosco/inventoryservice/base/RedisContainerTestBase;", "()V", "redisTemplate", "Lorg/springframework/data/redis/core/StringRedisTemplate;", "Redis \uc5f0\uacb0\uc774 \uc815\uc0c1\uc801\uc73c\ub85c \uc124\uc815\ub418\uc5b4\uc57c \ud55c\ub2e4", "", "Redis \ucee8\ud14c\uc774\ub108\uac00 \uc2e4\ud589 \uc911\uc774\uc5b4\uc57c \ud55c\ub2e4", "Redis\uc5d0 \ub370\uc774\ud130\ub97c \uc800\uc7a5\ud558\uace0 \uc870\ud68c\ud560 \uc218 \uc788\uc5b4\uc57c \ud55c\ub2e4", "Redis\uc5d0\uc11c \ud0a4\ub97c \uc0ad\uc81c\ud560 \uc218 \uc788\uc5b4\uc57c \ud55c\ub2e4", "inventory-service_test"})
public class RedisContainerTestBaseTest extends com.koosco.inventoryservice.base.RedisContainerTestBase {
    @org.springframework.beans.factory.annotation.Autowired()
    private org.springframework.data.redis.core.StringRedisTemplate redisTemplate;
    
    public RedisContainerTestBaseTest() {
        super();
    }
}