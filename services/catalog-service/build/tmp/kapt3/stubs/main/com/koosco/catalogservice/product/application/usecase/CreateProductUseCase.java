package com.koosco.catalogservice.product.application.usecase;

@com.koosco.common.core.annotation.UseCase()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0017\u0018\u00002\u00020\u0001B-\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\u000b\u00a2\u0006\u0002\u0010\fJ\u0010\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0013H\u0017R\u000e\u0010\u0004\u001a\u00020\u0005X\u0092\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0092\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\r\u001a\n \u000f*\u0004\u0018\u00010\u000e0\u000eX\u0092\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0092\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0092\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0092\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0014"}, d2 = {"Lcom/koosco/catalogservice/product/application/usecase/CreateProductUseCase;", "", "productRepository", "Lcom/koosco/catalogservice/product/application/port/ProductRepository;", "categoryRepository", "Lcom/koosco/catalogservice/category/application/repository/CategoryRepository;", "skuGenerator", "Lcom/koosco/catalogservice/product/domain/service/SkuGenerator;", "productValidator", "Lcom/koosco/catalogservice/product/domain/service/ProductValidator;", "integrationEventPublisher", "Lcom/koosco/catalogservice/product/application/port/IntegrationEventPublisher;", "(Lcom/koosco/catalogservice/product/application/port/ProductRepository;Lcom/koosco/catalogservice/category/application/repository/CategoryRepository;Lcom/koosco/catalogservice/product/domain/service/SkuGenerator;Lcom/koosco/catalogservice/product/domain/service/ProductValidator;Lcom/koosco/catalogservice/product/application/port/IntegrationEventPublisher;)V", "logger", "Lorg/slf4j/Logger;", "kotlin.jvm.PlatformType", "execute", "Lcom/koosco/catalogservice/product/application/result/ProductInfo;", "command", "Lcom/koosco/catalogservice/product/application/command/CreateProductCommand;", "catalog-service"})
public class CreateProductUseCase {
    @org.jetbrains.annotations.NotNull()
    private final com.koosco.catalogservice.product.application.port.ProductRepository productRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.koosco.catalogservice.category.application.repository.CategoryRepository categoryRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.koosco.catalogservice.product.domain.service.SkuGenerator skuGenerator = null;
    @org.jetbrains.annotations.NotNull()
    private final com.koosco.catalogservice.product.domain.service.ProductValidator productValidator = null;
    @org.jetbrains.annotations.NotNull()
    private final com.koosco.catalogservice.product.application.port.IntegrationEventPublisher integrationEventPublisher = null;
    private final org.slf4j.Logger logger = null;
    
    public CreateProductUseCase(@org.jetbrains.annotations.NotNull()
    com.koosco.catalogservice.product.application.port.ProductRepository productRepository, @org.jetbrains.annotations.NotNull()
    com.koosco.catalogservice.category.application.repository.CategoryRepository categoryRepository, @org.jetbrains.annotations.NotNull()
    com.koosco.catalogservice.product.domain.service.SkuGenerator skuGenerator, @org.jetbrains.annotations.NotNull()
    com.koosco.catalogservice.product.domain.service.ProductValidator productValidator, @org.jetbrains.annotations.NotNull()
    com.koosco.catalogservice.product.application.port.IntegrationEventPublisher integrationEventPublisher) {
        super();
    }
    
    @org.springframework.transaction.annotation.Transactional()
    @org.jetbrains.annotations.NotNull()
    public com.koosco.catalogservice.product.application.result.ProductInfo execute(@org.jetbrains.annotations.NotNull()
    com.koosco.catalogservice.product.application.command.CreateProductCommand command) {
        return null;
    }
}