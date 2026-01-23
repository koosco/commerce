package com.koosco.catalogservice.product.application.port;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\bf\u0018\u00002\u00020\u0001J7\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\u0010\u0005\u001a\u0004\u0018\u00010\u00062\b\u0010\u0007\u001a\u0004\u0018\u00010\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH&\u00a2\u0006\u0002\u0010\rJ\u0012\u0010\u000e\u001a\u0004\u0018\u00010\u00042\u0006\u0010\u000f\u001a\u00020\u0006H&J\u0012\u0010\u0010\u001a\u0004\u0018\u00010\u00042\u0006\u0010\u000f\u001a\u00020\u0006H&J\u0010\u0010\u0011\u001a\u00020\u00042\u0006\u0010\u0012\u001a\u00020\u0004H&\u00a8\u0006\u0013"}, d2 = {"Lcom/koosco/catalogservice/product/application/port/ProductRepository;", "", "findByConditions", "Lorg/springframework/data/domain/Page;", "Lcom/koosco/catalogservice/product/domain/entity/Product;", "categoryId", "", "keyword", "", "status", "Lcom/koosco/catalogservice/product/domain/enums/ProductStatus;", "pageable", "Lorg/springframework/data/domain/Pageable;", "(Ljava/lang/Long;Ljava/lang/String;Lcom/koosco/catalogservice/product/domain/enums/ProductStatus;Lorg/springframework/data/domain/Pageable;)Lorg/springframework/data/domain/Page;", "findByIdWithOptions", "productId", "findOrNull", "save", "product", "catalog-service"})
public abstract interface ProductRepository {
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.koosco.catalogservice.product.domain.entity.Product save(@org.jetbrains.annotations.NotNull()
    com.koosco.catalogservice.product.domain.entity.Product product);
    
    @org.jetbrains.annotations.Nullable()
    public abstract com.koosco.catalogservice.product.domain.entity.Product findOrNull(long productId);
    
    @org.jetbrains.annotations.Nullable()
    public abstract com.koosco.catalogservice.product.domain.entity.Product findByIdWithOptions(long productId);
    
    @org.jetbrains.annotations.NotNull()
    public abstract org.springframework.data.domain.Page<com.koosco.catalogservice.product.domain.entity.Product> findByConditions(@org.jetbrains.annotations.Nullable()
    java.lang.Long categoryId, @org.jetbrains.annotations.Nullable()
    java.lang.String keyword, @org.jetbrains.annotations.NotNull()
    com.koosco.catalogservice.product.domain.enums.ProductStatus status, @org.jetbrains.annotations.NotNull()
    org.springframework.data.domain.Pageable pageable);
}