package com.koosco.catalogservice.product.domain.entity;

@jakarta.persistence.Entity()
@jakarta.persistence.Table(name = "products")
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000T\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b$\n\u0002\u0010\u0002\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0005\b\u0017\u0018\u0000 D2\u00020\u0001:\u0001DB\u0097\u0001\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u0005\u0012\u0006\u0010\b\u001a\u00020\u0003\u0012\b\b\u0002\u0010\t\u001a\u00020\n\u0012\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\u0005\u0012\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\u0005\u0012\u000e\b\u0002\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00100\u000f\u0012\u000e\b\u0002\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00120\u000f\u0012\b\b\u0002\u0010\u0013\u001a\u00020\u0014\u0012\b\b\u0002\u0010\u0015\u001a\u00020\u0014\u00a2\u0006\u0002\u0010\u0016J\u0016\u00108\u001a\u0002092\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00100:H\u0016J\b\u0010;\u001a\u000209H\u0016J\u0013\u0010<\u001a\u00020=2\b\u0010>\u001a\u0004\u0018\u00010\u0001H\u0096\u0002J\b\u0010?\u001a\u00020@H\u0016J\b\u0010A\u001a\u000209H\u0017JS\u0010B\u001a\u0002092\b\u0010\u0006\u001a\u0004\u0018\u00010\u00052\b\u0010\u0007\u001a\u0004\u0018\u00010\u00052\b\u0010\b\u001a\u0004\u0018\u00010\u00032\b\u0010\t\u001a\u0004\u0018\u00010\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\u00032\b\u0010\f\u001a\u0004\u0018\u00010\u00052\b\u0010\r\u001a\u0004\u0018\u00010\u0005H\u0016\u00a2\u0006\u0002\u0010CR \u0010\r\u001a\u0004\u0018\u00010\u00058\u0016@\u0016X\u0097\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0017\u0010\u0018\"\u0004\b\u0019\u0010\u001aR\"\u0010\u000b\u001a\u0004\u0018\u00010\u00038\u0016@\u0016X\u0097\u000e\u00a2\u0006\u0010\n\u0002\u0010\u001f\u001a\u0004\b\u001b\u0010\u001c\"\u0004\b\u001d\u0010\u001eR\u0016\u0010\u0013\u001a\u00020\u00148\u0016X\u0097\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b \u0010!R \u0010\u0007\u001a\u0004\u0018\u00010\u00058\u0016@\u0016X\u0097\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\"\u0010\u0018\"\u0004\b#\u0010\u001aR\u001a\u0010\u0002\u001a\u0004\u0018\u00010\u00038\u0016X\u0097\u0004\u00a2\u0006\n\n\u0002\u0010\u001f\u001a\u0004\b$\u0010\u001cR\u001e\u0010\u0006\u001a\u00020\u00058\u0016@\u0016X\u0097\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b%\u0010\u0018\"\u0004\b&\u0010\u001aR\u001c\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00120\u000f8\u0016X\u0097\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\'\u0010(R\u001e\u0010\b\u001a\u00020\u00038\u0016@\u0016X\u0097\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b)\u0010*\"\u0004\b+\u0010,R\u0016\u0010\u0004\u001a\u00020\u00058\u0016X\u0097\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b-\u0010\u0018R\u001c\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00100\u000f8\u0016X\u0097\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b.\u0010(R\u001e\u0010\t\u001a\u00020\n8\u0016@\u0016X\u0097\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b/\u00100\"\u0004\b1\u00102R \u0010\f\u001a\u0004\u0018\u00010\u00058\u0016@\u0016X\u0097\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b3\u0010\u0018\"\u0004\b4\u0010\u001aR\u001e\u0010\u0015\u001a\u00020\u00148\u0016@\u0016X\u0097\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b5\u0010!\"\u0004\b6\u00107\u00a8\u0006E"}, d2 = {"Lcom/koosco/catalogservice/product/domain/entity/Product;", "", "id", "", "productCode", "", "name", "description", "price", "status", "Lcom/koosco/catalogservice/product/domain/enums/ProductStatus;", "categoryId", "thumbnailImageUrl", "brand", "skus", "", "Lcom/koosco/catalogservice/product/domain/entity/ProductSku;", "optionGroups", "Lcom/koosco/catalogservice/product/domain/entity/ProductOptionGroup;", "createdAt", "Ljava/time/LocalDateTime;", "updatedAt", "(Ljava/lang/Long;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;JLcom/koosco/catalogservice/product/domain/enums/ProductStatus;Ljava/lang/Long;Ljava/lang/String;Ljava/lang/String;Ljava/util/List;Ljava/util/List;Ljava/time/LocalDateTime;Ljava/time/LocalDateTime;)V", "getBrand", "()Ljava/lang/String;", "setBrand", "(Ljava/lang/String;)V", "getCategoryId", "()Ljava/lang/Long;", "setCategoryId", "(Ljava/lang/Long;)V", "Ljava/lang/Long;", "getCreatedAt", "()Ljava/time/LocalDateTime;", "getDescription", "setDescription", "getId", "getName", "setName", "getOptionGroups", "()Ljava/util/List;", "getPrice", "()J", "setPrice", "(J)V", "getProductCode", "getSkus", "getStatus", "()Lcom/koosco/catalogservice/product/domain/enums/ProductStatus;", "setStatus", "(Lcom/koosco/catalogservice/product/domain/enums/ProductStatus;)V", "getThumbnailImageUrl", "setThumbnailImageUrl", "getUpdatedAt", "setUpdatedAt", "(Ljava/time/LocalDateTime;)V", "addSkus", "", "", "delete", "equals", "", "other", "hashCode", "", "preUpdate", "update", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Long;Lcom/koosco/catalogservice/product/domain/enums/ProductStatus;Ljava/lang/Long;Ljava/lang/String;Ljava/lang/String;)V", "Companion", "catalog-service"})
public class Product {
    @jakarta.persistence.Id()
    @jakarta.persistence.GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Long id = null;
    @jakarta.persistence.Column(name = "product_code", nullable = false, unique = true, length = 50)
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String productCode = null;
    @jakarta.persistence.Column(nullable = false)
    @org.jetbrains.annotations.NotNull()
    private java.lang.String name;
    @jakarta.persistence.Column(columnDefinition = "TEXT")
    @org.jetbrains.annotations.Nullable()
    private java.lang.String description;
    
    /**
     * 상품 상세 페이지에 노출되는 기본 가격
     */
    @jakarta.persistence.Column(nullable = false)
    private long price;
    @jakarta.persistence.Enumerated(value = jakarta.persistence.EnumType.STRING)
    @jakarta.persistence.Column(nullable = false, length = 20)
    @org.jetbrains.annotations.NotNull()
    private com.koosco.catalogservice.product.domain.enums.ProductStatus status;
    @jakarta.persistence.Column(name = "category_id")
    @org.jetbrains.annotations.Nullable()
    private java.lang.Long categoryId;
    @jakarta.persistence.Column(name = "thumbnail_image_url", length = 500)
    @org.jetbrains.annotations.Nullable()
    private java.lang.String thumbnailImageUrl;
    @jakarta.persistence.Column(length = 100)
    @org.jetbrains.annotations.Nullable()
    private java.lang.String brand;
    @jakarta.persistence.OneToMany(mappedBy = "product", cascade = {jakarta.persistence.CascadeType.ALL}, orphanRemoval = true)
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.koosco.catalogservice.product.domain.entity.ProductSku> skus = null;
    @jakarta.persistence.OneToMany(mappedBy = "product", cascade = {jakarta.persistence.CascadeType.ALL}, orphanRemoval = true)
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.koosco.catalogservice.product.domain.entity.ProductOptionGroup> optionGroups = null;
    @jakarta.persistence.Column(name = "created_at", nullable = false, updatable = false)
    @org.jetbrains.annotations.NotNull()
    private final java.time.LocalDateTime createdAt = null;
    @jakarta.persistence.Column(name = "updated_at", nullable = false)
    @org.jetbrains.annotations.NotNull()
    private java.time.LocalDateTime updatedAt;
    @org.jetbrains.annotations.NotNull()
    public static final com.koosco.catalogservice.product.domain.entity.Product.Companion Companion = null;
    
    public Product(@org.jetbrains.annotations.Nullable()
    java.lang.Long id, @org.jetbrains.annotations.NotNull()
    java.lang.String productCode, @org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.Nullable()
    java.lang.String description, long price, @org.jetbrains.annotations.NotNull()
    com.koosco.catalogservice.product.domain.enums.ProductStatus status, @org.jetbrains.annotations.Nullable()
    java.lang.Long categoryId, @org.jetbrains.annotations.Nullable()
    java.lang.String thumbnailImageUrl, @org.jetbrains.annotations.Nullable()
    java.lang.String brand, @org.jetbrains.annotations.NotNull()
    java.util.List<com.koosco.catalogservice.product.domain.entity.ProductSku> skus, @org.jetbrains.annotations.NotNull()
    java.util.List<com.koosco.catalogservice.product.domain.entity.ProductOptionGroup> optionGroups, @org.jetbrains.annotations.NotNull()
    java.time.LocalDateTime createdAt, @org.jetbrains.annotations.NotNull()
    java.time.LocalDateTime updatedAt) {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public java.lang.Long getId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public java.lang.String getProductCode() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public java.lang.String getName() {
        return null;
    }
    
    public void setName(@org.jetbrains.annotations.NotNull()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public java.lang.String getDescription() {
        return null;
    }
    
    public void setDescription(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    /**
     * 상품 상세 페이지에 노출되는 기본 가격
     */
    public long getPrice() {
        return 0L;
    }
    
    /**
     * 상품 상세 페이지에 노출되는 기본 가격
     */
    public void setPrice(long p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public com.koosco.catalogservice.product.domain.enums.ProductStatus getStatus() {
        return null;
    }
    
    public void setStatus(@org.jetbrains.annotations.NotNull()
    com.koosco.catalogservice.product.domain.enums.ProductStatus p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public java.lang.Long getCategoryId() {
        return null;
    }
    
    public void setCategoryId(@org.jetbrains.annotations.Nullable()
    java.lang.Long p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public java.lang.String getThumbnailImageUrl() {
        return null;
    }
    
    public void setThumbnailImageUrl(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public java.lang.String getBrand() {
        return null;
    }
    
    public void setBrand(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public java.util.List<com.koosco.catalogservice.product.domain.entity.ProductSku> getSkus() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public java.util.List<com.koosco.catalogservice.product.domain.entity.ProductOptionGroup> getOptionGroups() {
        return null;
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
    
    public void update(@org.jetbrains.annotations.Nullable()
    java.lang.String name, @org.jetbrains.annotations.Nullable()
    java.lang.String description, @org.jetbrains.annotations.Nullable()
    java.lang.Long price, @org.jetbrains.annotations.Nullable()
    com.koosco.catalogservice.product.domain.enums.ProductStatus status, @org.jetbrains.annotations.Nullable()
    java.lang.Long categoryId, @org.jetbrains.annotations.Nullable()
    java.lang.String thumbnailImageUrl, @org.jetbrains.annotations.Nullable()
    java.lang.String brand) {
    }
    
    public void delete() {
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
    
    public void addSkus(@org.jetbrains.annotations.NotNull()
    java.util.List<? extends com.koosco.catalogservice.product.domain.entity.ProductSku> skus) {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002Jc\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\b\u0010\u0007\u001a\u0004\u0018\u00010\u00062\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\t2\b\u0010\r\u001a\u0004\u0018\u00010\u00062\b\u0010\u000e\u001a\u0004\u0018\u00010\u00062\b\u0010\u000f\u001a\u0004\u0018\u00010\u00062\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00120\u0011\u00a2\u0006\u0002\u0010\u0013J\u0010\u0010\u0014\u001a\u00020\u00062\b\u0010\r\u001a\u0004\u0018\u00010\u0006\u00a8\u0006\u0015"}, d2 = {"Lcom/koosco/catalogservice/product/domain/entity/Product$Companion;", "", "()V", "create", "Lcom/koosco/catalogservice/product/domain/entity/Product;", "name", "", "description", "price", "", "status", "Lcom/koosco/catalogservice/product/domain/enums/ProductStatus;", "categoryId", "categoryCode", "thumbnailImageUrl", "brand", "optionGroupSpecs", "", "Lcom/koosco/catalogservice/product/domain/vo/OptionGroupCreateSpec;", "(Ljava/lang/String;Ljava/lang/String;JLcom/koosco/catalogservice/product/domain/enums/ProductStatus;Ljava/lang/Long;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/util/List;)Lcom/koosco/catalogservice/product/domain/entity/Product;", "generate", "catalog-service"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.koosco.catalogservice.product.domain.entity.Product create(@org.jetbrains.annotations.NotNull()
        java.lang.String name, @org.jetbrains.annotations.Nullable()
        java.lang.String description, long price, @org.jetbrains.annotations.NotNull()
        com.koosco.catalogservice.product.domain.enums.ProductStatus status, @org.jetbrains.annotations.Nullable()
        java.lang.Long categoryId, @org.jetbrains.annotations.Nullable()
        java.lang.String categoryCode, @org.jetbrains.annotations.Nullable()
        java.lang.String thumbnailImageUrl, @org.jetbrains.annotations.Nullable()
        java.lang.String brand, @org.jetbrains.annotations.NotNull()
        java.util.List<com.koosco.catalogservice.product.domain.vo.OptionGroupCreateSpec> optionGroupSpecs) {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String generate(@org.jetbrains.annotations.Nullable()
        java.lang.String categoryCode) {
            return null;
        }
    }
}