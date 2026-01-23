package com.koosco.catalogservice.product.application.usecase;

@com.koosco.common.core.annotation.UseCase()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0017\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0010\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0017R\u000e\u0010\u0002\u001a\u00020\u0003X\u0092\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\t"}, d2 = {"Lcom/koosco/catalogservice/product/application/usecase/UpdateProductUseCase;", "", "productRepository", "Lcom/koosco/catalogservice/product/application/port/ProductRepository;", "(Lcom/koosco/catalogservice/product/application/port/ProductRepository;)V", "execute", "", "command", "Lcom/koosco/catalogservice/product/application/command/UpdateProductCommand;", "catalog-service"})
public class UpdateProductUseCase {
    @org.jetbrains.annotations.NotNull()
    private final com.koosco.catalogservice.product.application.port.ProductRepository productRepository = null;
    
    public UpdateProductUseCase(@org.jetbrains.annotations.NotNull()
    com.koosco.catalogservice.product.application.port.ProductRepository productRepository) {
        super();
    }
    
    @org.springframework.transaction.annotation.Transactional()
    public void execute(@org.jetbrains.annotations.NotNull()
    com.koosco.catalogservice.product.application.command.UpdateProductCommand command) {
    }
}