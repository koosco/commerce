package com.koosco.catalogservice.product.infra.persist.jpa;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\bf\u0018\u00002\u000e\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u00030\u0001J=\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00020\u00052\n\b\u0001\u0010\u0006\u001a\u0004\u0018\u00010\u00032\n\b\u0001\u0010\u0007\u001a\u0004\u0018\u00010\b2\b\b\u0001\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH\'\u00a2\u0006\u0002\u0010\rJ\u0014\u0010\u000e\u001a\u0004\u0018\u00010\u00022\b\b\u0001\u0010\u000f\u001a\u00020\u0003H\'\u00a8\u0006\u0010"}, d2 = {"Lcom/koosco/catalogservice/product/infra/persist/jpa/JpaProductRepository;", "Lorg/springframework/data/jpa/repository/JpaRepository;", "Lcom/koosco/catalogservice/product/domain/entity/Product;", "", "findByConditions", "Lorg/springframework/data/domain/Page;", "categoryId", "keyword", "", "status", "Lcom/koosco/catalogservice/product/domain/enums/ProductStatus;", "pageable", "Lorg/springframework/data/domain/Pageable;", "(Ljava/lang/Long;Ljava/lang/String;Lcom/koosco/catalogservice/product/domain/enums/ProductStatus;Lorg/springframework/data/domain/Pageable;)Lorg/springframework/data/domain/Page;", "findByIdWithOptions", "id", "catalog-service"})
public abstract interface JpaProductRepository extends org.springframework.data.jpa.repository.JpaRepository<com.koosco.catalogservice.product.domain.entity.Product, java.lang.Long> {
    
    @org.springframework.data.jpa.repository.Query(value = "\n        SELECT p FROM Product p\n        WHERE (:categoryId IS NULL OR p.categoryId = :categoryId)\n          AND (:keyword IS NULL OR p.name LIKE %:keyword% OR p.description LIKE %:keyword%)\n          AND p.status = :status\n        ")
    @org.jetbrains.annotations.NotNull()
    public abstract org.springframework.data.domain.Page<com.koosco.catalogservice.product.domain.entity.Product> findByConditions(@org.springframework.data.repository.query.Param(value = "categoryId")
    @org.jetbrains.annotations.Nullable()
    java.lang.Long categoryId, @org.springframework.data.repository.query.Param(value = "keyword")
    @org.jetbrains.annotations.Nullable()
    java.lang.String keyword, @org.springframework.data.repository.query.Param(value = "status")
    @org.jetbrains.annotations.NotNull()
    com.koosco.catalogservice.product.domain.enums.ProductStatus status, @org.jetbrains.annotations.NotNull()
    org.springframework.data.domain.Pageable pageable);
    
    @org.springframework.data.jpa.repository.Query(value = "SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.optionGroups WHERE p.id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract com.koosco.catalogservice.product.domain.entity.Product findByIdWithOptions(@org.springframework.data.repository.query.Param(value = "id")
    long id);
}