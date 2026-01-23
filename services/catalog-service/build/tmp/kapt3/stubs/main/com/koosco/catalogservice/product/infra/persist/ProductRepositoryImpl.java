package com.koosco.catalogservice.product.infra.persist;

@org.springframework.stereotype.Repository()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\b\u0017\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J7\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\b\u0010\b\u001a\u0004\u0018\u00010\t2\b\u0010\n\u001a\u0004\u0018\u00010\u000b2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000fH\u0016\u00a2\u0006\u0002\u0010\u0010J\u0012\u0010\u0011\u001a\u0004\u0018\u00010\u00072\u0006\u0010\u0012\u001a\u00020\tH\u0016J\u0012\u0010\u0013\u001a\u0004\u0018\u00010\u00072\u0006\u0010\u0012\u001a\u00020\tH\u0016J\u0010\u0010\u0014\u001a\u00020\u00072\u0006\u0010\u0015\u001a\u00020\u0007H\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0092\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0016"}, d2 = {"Lcom/koosco/catalogservice/product/infra/persist/ProductRepositoryImpl;", "Lcom/koosco/catalogservice/product/application/port/ProductRepository;", "jpaProductRepository", "Lcom/koosco/catalogservice/product/infra/persist/jpa/JpaProductRepository;", "(Lcom/koosco/catalogservice/product/infra/persist/jpa/JpaProductRepository;)V", "findByConditions", "Lorg/springframework/data/domain/Page;", "Lcom/koosco/catalogservice/product/domain/entity/Product;", "categoryId", "", "keyword", "", "status", "Lcom/koosco/catalogservice/product/domain/enums/ProductStatus;", "pageable", "Lorg/springframework/data/domain/Pageable;", "(Ljava/lang/Long;Ljava/lang/String;Lcom/koosco/catalogservice/product/domain/enums/ProductStatus;Lorg/springframework/data/domain/Pageable;)Lorg/springframework/data/domain/Page;", "findByIdWithOptions", "productId", "findOrNull", "save", "product", "catalog-service"})
public class ProductRepositoryImpl implements com.koosco.catalogservice.product.application.port.ProductRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.koosco.catalogservice.product.infra.persist.jpa.JpaProductRepository jpaProductRepository = null;
    
    public ProductRepositoryImpl(@org.jetbrains.annotations.NotNull()
    com.koosco.catalogservice.product.infra.persist.jpa.JpaProductRepository jpaProductRepository) {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public com.koosco.catalogservice.product.domain.entity.Product save(@org.jetbrains.annotations.NotNull()
    com.koosco.catalogservice.product.domain.entity.Product product) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public com.koosco.catalogservice.product.domain.entity.Product findOrNull(long productId) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public com.koosco.catalogservice.product.domain.entity.Product findByIdWithOptions(long productId) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public org.springframework.data.domain.Page<com.koosco.catalogservice.product.domain.entity.Product> findByConditions(@org.jetbrains.annotations.Nullable()
    java.lang.Long categoryId, @org.jetbrains.annotations.Nullable()
    java.lang.String keyword, @org.jetbrains.annotations.NotNull()
    com.koosco.catalogservice.product.domain.enums.ProductStatus status, @org.jetbrains.annotations.NotNull()
    org.springframework.data.domain.Pageable pageable) {
        return null;
    }
}