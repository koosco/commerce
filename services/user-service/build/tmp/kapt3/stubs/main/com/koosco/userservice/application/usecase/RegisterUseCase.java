package com.koosco.userservice.application.usecase;

@com.koosco.common.core.annotation.UseCase()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0017\u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bJ\u0010\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000fH\u0012J\u0010\u0010\u0010\u001a\u00020\r2\u0006\u0010\u0011\u001a\u00020\u0012H\u0016J\u0010\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0011\u001a\u00020\u0012H\u0012R\u000e\u0010\u0004\u001a\u00020\u0005X\u0092\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\t\u001a\n \u000b*\u0004\u0018\u00010\n0\nX\u0092\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0092\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0092\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0015"}, d2 = {"Lcom/koosco/userservice/application/usecase/RegisterUseCase;", "", "userRepository", "Lcom/koosco/userservice/application/repository/UserRepository;", "authServiceClient", "Lcom/koosco/userservice/application/port/AuthServiceClient;", "transactionRunner", "Lcom/koosco/common/core/transaction/TransactionRunner;", "(Lcom/koosco/userservice/application/repository/UserRepository;Lcom/koosco/userservice/application/port/AuthServiceClient;Lcom/koosco/common/core/transaction/TransactionRunner;)V", "logger", "Lorg/slf4j/Logger;", "kotlin.jvm.PlatformType", "deleteById", "", "userId", "", "execute", "command", "Lcom/koosco/userservice/application/command/CreateUserCommand;", "registerUser", "Lcom/koosco/userservice/domain/entity/User;", "user-service"})
public class RegisterUseCase {
    @org.jetbrains.annotations.NotNull()
    private final com.koosco.userservice.application.repository.UserRepository userRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.koosco.userservice.application.port.AuthServiceClient authServiceClient = null;
    @org.jetbrains.annotations.NotNull()
    private final com.koosco.common.core.transaction.TransactionRunner transactionRunner = null;
    private final org.slf4j.Logger logger = null;
    
    public RegisterUseCase(@org.jetbrains.annotations.NotNull()
    com.koosco.userservice.application.repository.UserRepository userRepository, @org.jetbrains.annotations.NotNull()
    com.koosco.userservice.application.port.AuthServiceClient authServiceClient, @org.jetbrains.annotations.NotNull()
    com.koosco.common.core.transaction.TransactionRunner transactionRunner) {
        super();
    }
    
    public void execute(@org.jetbrains.annotations.NotNull()
    com.koosco.userservice.application.command.CreateUserCommand command) {
    }
    
    private com.koosco.userservice.domain.entity.User registerUser(com.koosco.userservice.application.command.CreateUserCommand command) {
        return null;
    }
    
    private void deleteById(long userId) {
    }
}