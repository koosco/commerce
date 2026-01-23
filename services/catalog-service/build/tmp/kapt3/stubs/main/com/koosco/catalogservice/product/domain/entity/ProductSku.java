package com.koosco.catalogservice.product.domain.entity;

@jakarta.persistence.Entity()
@jakarta.persistence.Table(name = "product_skus")
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0011\b\u0017\u0018\u0000 \u001b2\u00020\u0001:\u0001\u001bB;\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\u0003\u0012\u0006\u0010\t\u001a\u00020\u0005\u0012\b\b\u0002\u0010\n\u001a\u00020\u000b\u00a2\u0006\u0002\u0010\fR\u0016\u0010\n\u001a\u00020\u000b8\u0016X\u0097\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u001a\u0010\u0002\u001a\u0004\u0018\u00010\u00038\u0016X\u0097\u0004\u00a2\u0006\n\n\u0002\u0010\u0011\u001a\u0004\b\u000f\u0010\u0010R\u0016\u0010\t\u001a\u00020\u00058\u0016X\u0097\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0016\u0010\b\u001a\u00020\u00038\u0016X\u0097\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u001e\u0010\u0006\u001a\u00020\u00078\u0016@\u0016X\u0097\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0016\u0010\u0017\"\u0004\b\u0018\u0010\u0019R\u0016\u0010\u0004\u001a\u00020\u00058\u0016X\u0097\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u0013\u00a8\u0006\u001c"}, d2 = {"Lcom/koosco/catalogservice/product/domain/entity/ProductSku;", "", "id", "", "skuId", "", "product", "Lcom/koosco/catalogservice/product/domain/entity/Product;", "price", "optionValues", "createdAt", "Ljava/time/LocalDateTime;", "(Ljava/lang/Long;Ljava/lang/String;Lcom/koosco/catalogservice/product/domain/entity/Product;JLjava/lang/String;Ljava/time/LocalDateTime;)V", "getCreatedAt", "()Ljava/time/LocalDateTime;", "getId", "()Ljava/lang/Long;", "Ljava/lang/Long;", "getOptionValues", "()Ljava/lang/String;", "getPrice", "()J", "getProduct", "()Lcom/koosco/catalogservice/product/domain/entity/Product;", "setProduct", "(Lcom/koosco/catalogservice/product/domain/entity/Product;)V", "getSkuId", "Companion", "catalog-service"})
public class ProductSku {
    @jakarta.persistence.Id()
    @jakarta.persistence.GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Long id = null;
    
    /**
     * 문자열 기반 SKU ID
     */
    @jakarta.persistence.Column(name = "sku_id", nullable = false, unique = true, length = 100)
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String skuId = null;
    @jakarta.persistence.ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @jakarta.persistence.JoinColumn(name = "product_id", nullable = false)
    @org.jetbrains.annotations.NotNull()
    private com.koosco.catalogservice.product.domain.entity.Product product;
    
    /**
     * 장바구니, 주문 생성, 재고 감소 등 "실거래에 사용되는 가격"
     */
    @jakarta.persistence.Column(nullable = false)
    private final long price = 0L;
    
    /**
     * 옵션 조합 — JSON 형태
     */
    @jakarta.persistence.Column(name = "option_values", columnDefinition = "JSON")
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String optionValues = null;
    @jakarta.persistence.Column(name = "created_at", nullable = false, updatable = false)
    @org.jetbrains.annotations.NotNull()
    private final java.time.LocalDateTime createdAt = null;
    @org.jetbrains.annotations.NotNull()
    private static final com.fasterxml.jackson.databind.ObjectMapper objectMapper = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.koosco.catalogservice.product.domain.entity.ProductSku.Companion Companion = null;
    
    public ProductSku(@org.jetbrains.annotations.Nullable()
    java.lang.Long id, @org.jetbrains.annotations.NotNull()
    java.lang.String skuId, @org.jetbrains.annotations.NotNull()
    com.koosco.catalogservice.product.domain.entity.Product product, long price, @org.jetbrains.annotations.NotNull()
    java.lang.String optionValues, @org.jetbrains.annotations.NotNull()
    java.time.LocalDateTime createdAt) {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public java.lang.Long getId() {
        return null;
    }
    
    /**
     * 문자열 기반 SKU ID
     */
    @org.jetbrains.annotations.NotNull()
    public java.lang.String getSkuId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public com.koosco.catalogservice.product.domain.entity.Product getProduct() {
        return null;
    }
    
    public void setProduct(@org.jetbrains.annotations.NotNull()
    com.koosco.catalogservice.product.domain.entity.Product p0) {
    }
    
    /**
     * 장바구니, 주문 생성, 재고 감소 등 "실거래에 사용되는 가격"
     */
    public long getPrice() {
        return 0L;
    }
    
    /**
     * 옵션 조합 — JSON 형태
     */
    @org.jetbrains.annotations.NotNull()
    public java.lang.String getOptionValues() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public java.time.LocalDateTime getCreatedAt() {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J*\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0012\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\u000b0\n2\u0006\u0010\f\u001a\u00020\rJ\"\u0010\u000e\u001a\u00020\u000b2\u0006\u0010\u000f\u001a\u00020\u000b2\u0012\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\u000b0\nR\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0010"}, d2 = {"Lcom/koosco/catalogservice/product/domain/entity/ProductSku$Companion;", "", "()V", "objectMapper", "Lcom/fasterxml/jackson/databind/ObjectMapper;", "create", "Lcom/koosco/catalogservice/product/domain/entity/ProductSku;", "product", "Lcom/koosco/catalogservice/product/domain/entity/Product;", "options", "", "", "price", "", "generate", "productCode", "catalog-service"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.koosco.catalogservice.product.domain.entity.ProductSku create(@org.jetbrains.annotations.NotNull()
        com.koosco.catalogservice.product.domain.entity.Product product, @org.jetbrains.annotations.NotNull()
        java.util.Map<java.lang.String, java.lang.String> options, long price) {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String generate(@org.jetbrains.annotations.NotNull()
        java.lang.String productCode, @org.jetbrains.annotations.NotNull()
        java.util.Map<java.lang.String, java.lang.String> options) {
            return null;
        }
    }
}