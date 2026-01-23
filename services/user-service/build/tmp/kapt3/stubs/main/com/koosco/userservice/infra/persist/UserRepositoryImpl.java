package com.koosco.userservice.infra.persist;

@org.springframework.stereotype.Repository()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0017\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u0010\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eH\u0016J\u0012\u0010\u000f\u001a\u0004\u0018\u00010\u00102\u0006\u0010\r\u001a\u00020\u000eH\u0016J\u0010\u0010\u0011\u001a\u00020\u00102\u0006\u0010\u0012\u001a\u00020\u0010H\u0016R\u0014\u0010\u0002\u001a\u00020\u0003X\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0014\u0010\u0004\u001a\u00020\u0005X\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\n\u00a8\u0006\u0013"}, d2 = {"Lcom/koosco/userservice/infra/persist/UserRepositoryImpl;", "Lcom/koosco/userservice/application/repository/UserRepository;", "jpaUserRepository", "Lcom/koosco/userservice/infra/persist/JpaUserRepository;", "userQuery", "Lcom/koosco/userservice/infra/persist/UserQuery;", "(Lcom/koosco/userservice/infra/persist/JpaUserRepository;Lcom/koosco/userservice/infra/persist/UserQuery;)V", "getJpaUserRepository", "()Lcom/koosco/userservice/infra/persist/JpaUserRepository;", "getUserQuery", "()Lcom/koosco/userservice/infra/persist/UserQuery;", "deleteById", "", "userId", "", "findActiveUserById", "Lcom/koosco/userservice/domain/entity/User;", "save", "user", "user-service"})
public class UserRepositoryImpl implements com.koosco.userservice.application.repository.UserRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.koosco.userservice.infra.persist.JpaUserRepository jpaUserRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.koosco.userservice.infra.persist.UserQuery userQuery = null;
    
    public UserRepositoryImpl(@org.jetbrains.annotations.NotNull()
    com.koosco.userservice.infra.persist.JpaUserRepository jpaUserRepository, @org.jetbrains.annotations.NotNull()
    com.koosco.userservice.infra.persist.UserQuery userQuery) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public com.koosco.userservice.infra.persist.JpaUserRepository getJpaUserRepository() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public com.koosco.userservice.infra.persist.UserQuery getUserQuery() {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public com.koosco.userservice.domain.entity.User save(@org.jetbrains.annotations.NotNull()
    com.koosco.userservice.domain.entity.User user) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public com.koosco.userservice.domain.entity.User findActiveUserById(long userId) {
        return null;
    }
    
    @java.lang.Override()
    public void deleteById(long userId) {
    }
}