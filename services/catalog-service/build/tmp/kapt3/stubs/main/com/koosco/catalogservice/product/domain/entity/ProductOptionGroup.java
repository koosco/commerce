package com.koosco.catalogservice.product.domain.entity;

@jakarta.persistence.Entity()
@jakarta.persistence.Table(name = "product_option_groups")
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\b\u0018\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0005\b\u0017\u0018\u0000 /2\u00020\u0001:\u0001/BS\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\t\u0012\b\b\u0002\u0010\n\u001a\u00020\u000b\u0012\b\b\u0002\u0010\f\u001a\u00020\u000b\u0012\u000e\b\u0002\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000e\u00a2\u0006\u0002\u0010\u0010J\u0010\u0010\'\u001a\u00020(2\u0006\u0010)\u001a\u00020\u000fH\u0016J\u0013\u0010*\u001a\u00020+2\b\u0010,\u001a\u0004\u0018\u00010\u0001H\u0096\u0002J\b\u0010-\u001a\u00020\tH\u0016J\b\u0010.\u001a\u00020(H\u0017R\u0016\u0010\n\u001a\u00020\u000b8\u0016X\u0097\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u001a\u0010\u0002\u001a\u0004\u0018\u00010\u00038\u0016X\u0097\u0004\u00a2\u0006\n\n\u0002\u0010\u0015\u001a\u0004\b\u0013\u0010\u0014R\u001e\u0010\u0006\u001a\u00020\u00078\u0016@\u0016X\u0097\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0016\u0010\u0017\"\u0004\b\u0018\u0010\u0019R\u001c\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000e8\u0016X\u0097\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u001bR\u001e\u0010\b\u001a\u00020\t8\u0016@\u0016X\u0097\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u001c\u0010\u001d\"\u0004\b\u001e\u0010\u001fR \u0010\u0004\u001a\u0004\u0018\u00010\u00058\u0016@\u0016X\u0097\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b \u0010!\"\u0004\b\"\u0010#R\u001e\u0010\f\u001a\u00020\u000b8\u0016@\u0016X\u0097\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b$\u0010\u0012\"\u0004\b%\u0010&\u00a8\u00060"}, d2 = {"Lcom/koosco/catalogservice/product/domain/entity/ProductOptionGroup;", "", "id", "", "product", "Lcom/koosco/catalogservice/product/domain/entity/Product;", "name", "", "ordering", "", "createdAt", "Ljava/time/LocalDateTime;", "updatedAt", "options", "", "Lcom/koosco/catalogservice/product/domain/entity/ProductOption;", "(Ljava/lang/Long;Lcom/koosco/catalogservice/product/domain/entity/Product;Ljava/lang/String;ILjava/time/LocalDateTime;Ljava/time/LocalDateTime;Ljava/util/List;)V", "getCreatedAt", "()Ljava/time/LocalDateTime;", "getId", "()Ljava/lang/Long;", "Ljava/lang/Long;", "getName", "()Ljava/lang/String;", "setName", "(Ljava/lang/String;)V", "getOptions", "()Ljava/util/List;", "getOrdering", "()I", "setOrdering", "(I)V", "getProduct", "()Lcom/koosco/catalogservice/product/domain/entity/Product;", "setProduct", "(Lcom/koosco/catalogservice/product/domain/entity/Product;)V", "getUpdatedAt", "setUpdatedAt", "(Ljava/time/LocalDateTime;)V", "addOption", "", "option", "equals", "", "other", "hashCode", "preUpdate", "Companion", "catalog-service"})
public class ProductOptionGroup {
    @jakarta.persistence.Id()
    @jakarta.persistence.GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Long id = null;
    @jakarta.persistence.ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @jakarta.persistence.JoinColumn(name = "product_id", nullable = false)
    @org.jetbrains.annotations.Nullable()
    private com.koosco.catalogservice.product.domain.entity.Product product;
    @jakarta.persistence.Column(nullable = false, length = 100)
    @org.jetbrains.annotations.NotNull()
    private java.lang.String name;
    @jakarta.persistence.Column(nullable = false)
    private int ordering;
    @jakarta.persistence.Column(name = "created_at", nullable = false, updatable = false)
    @org.jetbrains.annotations.NotNull()
    private final java.time.LocalDateTime createdAt = null;
    @jakarta.persistence.Column(name = "updated_at", nullable = false)
    @org.jetbrains.annotations.NotNull()
    private java.time.LocalDateTime updatedAt;
    @jakarta.persistence.OneToMany(mappedBy = "optionGroup", cascade = {jakarta.persistence.CascadeType.ALL}, orphanRemoval = true)
    @org.hibernate.annotations.BatchSize(size = 10)
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.koosco.catalogservice.product.domain.entity.ProductOption> options = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.koosco.catalogservice.product.domain.entity.ProductOptionGroup.Companion Companion = null;
    
    public ProductOptionGroup(@org.jetbrains.annotations.Nullable()
    java.lang.Long id, @org.jetbrains.annotations.Nullable()
    com.koosco.catalogservice.product.domain.entity.Product product, @org.jetbrains.annotations.NotNull()
    java.lang.String name, int ordering, @org.jetbrains.annotations.NotNull()
    java.time.LocalDateTime createdAt, @org.jetbrains.annotations.NotNull()
    java.time.LocalDateTime updatedAt, @org.jetbrains.annotations.NotNull()
    java.util.List<com.koosco.catalogservice.product.domain.entity.ProductOption> options) {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public java.lang.Long getId() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public com.koosco.catalogservice.product.domain.entity.Product getProduct() {
        return null;
    }
    
    public void setProduct(@org.jetbrains.annotations.Nullable()
    com.koosco.catalogservice.product.domain.entity.Product p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public java.lang.String getName() {
        return null;
    }
    
    public void setName(@org.jetbrains.annotations.NotNull()
    java.lang.String p0) {
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
    
    @org.jetbrains.annotations.NotNull()
    public java.util.List<com.koosco.catalogservice.product.domain.entity.ProductOption> getOptions() {
        return null;
    }
    
    @jakarta.persistence.PreUpdate()
    public void preUpdate() {
    }
    
    public void addOption(@org.jetbrains.annotations.NotNull()
    com.koosco.catalogservice.product.domain.entity.ProductOption option) {
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
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J$\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\n\u00a8\u0006\f"}, d2 = {"Lcom/koosco/catalogservice/product/domain/entity/ProductOptionGroup$Companion;", "", "()V", "create", "Lcom/koosco/catalogservice/product/domain/entity/ProductOptionGroup;", "name", "", "ordering", "", "optionSpecs", "", "Lcom/koosco/catalogservice/product/domain/vo/CreateOptionSpec;", "catalog-service"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.koosco.catalogservice.product.domain.entity.ProductOptionGroup create(@org.jetbrains.annotations.NotNull()
        java.lang.String name, int ordering, @org.jetbrains.annotations.NotNull()
        java.util.List<com.koosco.catalogservice.product.domain.vo.CreateOptionSpec> optionSpecs) {
            return null;
        }
    }
}