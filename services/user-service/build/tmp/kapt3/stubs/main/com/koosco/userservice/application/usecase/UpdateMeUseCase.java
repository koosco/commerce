package com.koosco.userservice.application.usecase;

@com.koosco.common.core.annotation.UseCase()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0017\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0010\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0017R\u000e\u0010\u0002\u001a\u00020\u0003X\u0092\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\t"}, d2 = {"Lcom/koosco/userservice/application/usecase/UpdateMeUseCase;", "", "userRepository", "Lcom/koosco/userservice/application/repository/UserRepository;", "(Lcom/koosco/userservice/application/repository/UserRepository;)V", "execute", "", "command", "Lcom/koosco/userservice/application/command/UpdateUserCommand;", "user-service"})
public class UpdateMeUseCase {
    @org.jetbrains.annotations.NotNull()
    private final com.koosco.userservice.application.repository.UserRepository userRepository = null;
    
    public UpdateMeUseCase(@org.jetbrains.annotations.NotNull()
    com.koosco.userservice.application.repository.UserRepository userRepository) {
        super();
    }
    
    @org.springframework.transaction.annotation.Transactional()
    public void execute(@org.jetbrains.annotations.NotNull()
    com.koosco.userservice.application.command.UpdateUserCommand command) {
    }
}