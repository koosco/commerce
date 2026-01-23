package com.koosco.userservice.application.repository;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\bf\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&J\u0012\u0010\u0006\u001a\u0004\u0018\u00010\u00072\u0006\u0010\u0004\u001a\u00020\u0005H&J\u0010\u0010\b\u001a\u00020\u00072\u0006\u0010\t\u001a\u00020\u0007H&\u00a8\u0006\n"}, d2 = {"Lcom/koosco/userservice/application/repository/UserRepository;", "", "deleteById", "", "userId", "", "findActiveUserById", "Lcom/koosco/userservice/domain/entity/User;", "save", "user", "user-service"})
public abstract interface UserRepository {
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.koosco.userservice.domain.entity.User save(@org.jetbrains.annotations.NotNull()
    com.koosco.userservice.domain.entity.User user);
    
    @org.jetbrains.annotations.Nullable()
    public abstract com.koosco.userservice.domain.entity.User findActiveUserById(long userId);
    
    public abstract void deleteById(long userId);
}