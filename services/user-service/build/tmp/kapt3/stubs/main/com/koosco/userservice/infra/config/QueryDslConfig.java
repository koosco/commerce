package com.koosco.userservice.infra.config;

@org.springframework.context.annotation.Configuration()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\b\u0017\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\b\u0010\u0007\u001a\u00020\bH\u0017R\u0016\u0010\u0002\u001a\u00020\u00038\u0016X\u0097\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\t"}, d2 = {"Lcom/koosco/userservice/infra/config/QueryDslConfig;", "", "entityManager", "Ljakarta/persistence/EntityManager;", "(Ljakarta/persistence/EntityManager;)V", "getEntityManager", "()Ljakarta/persistence/EntityManager;", "queryFactory", "Lcom/querydsl/jpa/impl/JPAQueryFactory;", "user-service"})
public class QueryDslConfig {
    @jakarta.persistence.PersistenceContext()
    @org.jetbrains.annotations.NotNull()
    private final jakarta.persistence.EntityManager entityManager = null;
    
    public QueryDslConfig(@org.jetbrains.annotations.NotNull()
    jakarta.persistence.EntityManager entityManager) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public jakarta.persistence.EntityManager getEntityManager() {
        return null;
    }
    
    @org.springframework.context.annotation.Bean()
    @org.jetbrains.annotations.NotNull()
    public com.querydsl.jpa.impl.JPAQueryFactory queryFactory() {
        return null;
    }
}