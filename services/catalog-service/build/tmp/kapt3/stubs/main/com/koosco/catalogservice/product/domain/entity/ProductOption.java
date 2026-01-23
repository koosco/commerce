package com.koosco.catalogservice.product.domain.entity;

@jakarta.persistence.Entity()
@jakarta.persistence.Table(name = "product_options")
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u001b\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\b\u0017\u0018\u00002\u00020\u0001BM\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\u0003\u0012\b\b\u0002\u0010\t\u001a\u00020\n\u0012\b\b\u0002\u0010\u000b\u001a\u00020\f\u0012\b\b\u0002\u0010\r\u001a\u00020\f\u00a2\u0006\u0002\u0010\u000eJ\u0013\u0010\'\u001a\u00020(2\b\u0010)\u001a\u0004\u0018\u00010\u0001H\u0096\u0002J\b\u0010*\u001a\u00020\nH\u0016J\b\u0010+\u001a\u00020,H\u0017R\u001e\u0010\b\u001a\u00020\u00038\u0016@\u0016X\u0097\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u000f\u0010\u0010\"\u0004\b\u0011\u0010\u0012R\u0016\u0010\u000b\u001a\u00020\f8\u0016X\u0097\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u001a\u0010\u0002\u001a\u0004\u0018\u00010\u00038\u0016X\u0097\u0004\u00a2\u0006\n\n\u0002\u0010\u0017\u001a\u0004\b\u0015\u0010\u0016R\u001e\u0010\u0006\u001a\u00020\u00078\u0016@\u0016X\u0097\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0018\u0010\u0019\"\u0004\b\u001a\u0010\u001bR \u0010\u0004\u001a\u0004\u0018\u00010\u00058\u0016@\u0016X\u0097\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u001c\u0010\u001d\"\u0004\b\u001e\u0010\u001fR\u001e\u0010\t\u001a\u00020\n8\u0016@\u0016X\u0097\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b \u0010!\"\u0004\b\"\u0010#R\u001e\u0010\r\u001a\u00020\f8\u0016@\u0016X\u0097\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b$\u0010\u0014\"\u0004\b%\u0010&\u00a8\u0006-"}, d2 = {"Lcom/koosco/catalogservice/product/domain/entity/ProductOption;", "", "id", "", "optionGroup", "Lcom/koosco/catalogservice/product/domain/entity/ProductOptionGroup;", "name", "", "additionalPrice", "ordering", "", "createdAt", "Ljava/time/LocalDateTime;", "updatedAt", "(Ljava/lang/Long;Lcom/koosco/catalogservice/product/domain/entity/ProductOptionGroup;Ljava/lang/String;JILjava/time/LocalDateTime;Ljava/time/LocalDateTime;)V", "getAdditionalPrice", "()J", "setAdditionalPrice", "(J)V", "getCreatedAt", "()Ljava/time/LocalDateTime;", "getId", "()Ljava/lang/Long;", "Ljava/lang/Long;", "getName", "()Ljava/lang/String;", "setName", "(Ljava/lang/String;)V", "getOptionGroup", "()Lcom/koosco/catalogservice/product/domain/entity/ProductOptionGroup;", "setOptionGroup", "(Lcom/koosco/catalogservice/product/domain/entity/ProductOptionGroup;)V", "getOrdering", "()I", "setOrdering", "(I)V", "getUpdatedAt", "setUpdatedAt", "(Ljava/time/LocalDateTime;)V", "equals", "", "other", "hashCode", "preUpdate", "", "catalog-service"})
public class ProductOption {
    @jakarta.persistence.Id()
    @jakarta.persistence.GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Long id = null;
    @jakarta.persistence.ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @jakarta.persistence.JoinColumn(name = "option_group_id", nullable = false)
    @org.jetbrains.annotations.Nullable()
    private com.koosco.catalogservice.product.domain.entity.ProductOptionGroup optionGroup;
    @jakarta.persistence.Column(nullable = false, length = 100)
    @org.jetbrains.annotations.NotNull()
    private java.lang.String name;
    
    /**
     * 옵션 선택 시 추가되는 금액
     */
    @jakarta.persistence.Column(name = "additional_price", nullable = false)
    private long additionalPrice;
    @jakarta.persistence.Column(nullable = false)
    private int ordering;
    @jakarta.persistence.Column(name = "created_at", nullable = false, updatable = false)
    @org.jetbrains.annotations.NotNull()
    private final java.time.LocalDateTime createdAt = null;
    @jakarta.persistence.Column(name = "updated_at", nullable = false)
    @org.jetbrains.annotations.NotNull()
    private java.time.LocalDateTime updatedAt;
    
    public ProductOption(@org.jetbrains.annotations.Nullable()
    java.lang.Long id, @org.jetbrains.annotations.Nullable()
    com.koosco.catalogservice.product.domain.entity.ProductOptionGroup optionGroup, @org.jetbrains.annotations.NotNull()
    java.lang.String name, long additionalPrice, int ordering, @org.jetbrains.annotations.NotNull()
    java.time.LocalDateTime createdAt, @org.jetbrains.annotations.NotNull()
    java.time.LocalDateTime updatedAt) {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public java.lang.Long getId() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public com.koosco.catalogservice.product.domain.entity.ProductOptionGroup getOptionGroup() {
        return null;
    }
    
    public void setOptionGroup(@org.jetbrains.annotations.Nullable()
    com.koosco.catalogservice.product.domain.entity.ProductOptionGroup p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public java.lang.String getName() {
        return null;
    }
    
    public void setName(@org.jetbrains.annotations.NotNull()
    java.lang.String p0) {
    }
    
    /**
     * 옵션 선택 시 추가되는 금액
     */
    public long getAdditionalPrice() {
        return 0L;
    }
    
    /**
     * 옵션 선택 시 추가되는 금액
     */
    public void setAdditionalPrice(long p0) {
    }
    
    public int getOrdering() {
        return 0;
    }
    
    public void setOrdering(int p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public java.time.LocalDateTime getCreatedAt() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public java.time.LocalDateTime getUpdatedAt() {
        return null;
    }
    
    public void setUpdatedAt(@org.jetbrains.annotations.NotNull()
    java.time.LocalDateTime p0) {
    }
    
    @jakarta.persistence.PreUpdate()
    public void preUpdate() {
    }
    
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
}