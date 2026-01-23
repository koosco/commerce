package com.koosco.catalogservice.product.api;

/**
 * fileName       : ProductResponses
 * author         : koo
 * date           : 2025. 12. 22. 오전 9:20
 * description    :
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0017\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\b\u0086\b\u0018\u0000 %2\u00020\u0001:\u0001%B9\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\b\u0010\t\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\n\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\u0002\u0010\u000bJ\t\u0010\u0017\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0018\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0019\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001a\u001a\u00020\bH\u00c6\u0003J\u0010\u0010\u001b\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003\u00a2\u0006\u0002\u0010\rJ\u000b\u0010\u001c\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003JN\u0010\u001d\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\b2\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u0005H\u00c6\u0001\u00a2\u0006\u0002\u0010\u001eJ\u0013\u0010\u001f\u001a\u00020 2\b\u0010!\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\"\u001a\u00020#H\u00d6\u0001J\t\u0010$\u001a\u00020\u0005H\u00d6\u0001R\u0015\u0010\t\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\n\n\u0002\u0010\u000e\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0010R\u0011\u0010\u0007\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0013\u0010\n\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0012\u00a8\u0006&"}, d2 = {"Lcom/koosco/catalogservice/product/api/ProductListResponse;", "", "id", "", "name", "", "price", "status", "Lcom/koosco/catalogservice/product/domain/enums/ProductStatus;", "categoryId", "thumbnailImageUrl", "(JLjava/lang/String;JLcom/koosco/catalogservice/product/domain/enums/ProductStatus;Ljava/lang/Long;Ljava/lang/String;)V", "getCategoryId", "()Ljava/lang/Long;", "Ljava/lang/Long;", "getId", "()J", "getName", "()Ljava/lang/String;", "getPrice", "getStatus", "()Lcom/koosco/catalogservice/product/domain/enums/ProductStatus;", "getThumbnailImageUrl", "component1", "component2", "component3", "component4", "component5", "component6", "copy", "(JLjava/lang/String;JLcom/koosco/catalogservice/product/domain/enums/ProductStatus;Ljava/lang/Long;Ljava/lang/String;)Lcom/koosco/catalogservice/product/api/ProductListResponse;", "equals", "", "other", "hashCode", "", "toString", "Companion", "catalog-service"})
public final class ProductListResponse {
    private final long id = 0L;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String name = null;
    private final long price = 0L;
    @org.jetbrains.annotations.NotNull()
    private final com.koosco.catalogservice.product.domain.enums.ProductStatus status = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Long categoryId = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String thumbnailImageUrl = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.koosco.catalogservice.product.api.ProductListResponse.Companion Companion = null;
    
    public ProductListResponse(long id, @org.jetbrains.annotations.NotNull()
    java.lang.String name, long price, @org.jetbrains.annotations.NotNull()
    com.koosco.catalogservice.product.domain.enums.ProductStatus status, @org.jetbrains.annotations.Nullable()
    java.lang.Long categoryId, @org.jetbrains.annotations.Nullable()
    java.lang.String thumbnailImageUrl) {
        super();
    }
    
    public final long getId() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getName() {
        return null;
    }
    
    public final long getPrice() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.koosco.catalogservice.product.domain.enums.ProductStatus getStatus() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Long getCategoryId() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getThumbnailImageUrl() {
        return null;
    }
    
    public final long component1() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component2() {
        return null;
    }
    
    public final long component3() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.koosco.catalogservice.product.domain.enums.ProductStatus component4() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Long component5() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component6() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.koosco.catalogservice.product.api.ProductListResponse copy(long id, @org.jetbrains.annotations.NotNull()
    java.lang.String name, long price, @org.jetbrains.annotations.NotNull()
    com.koosco.catalogservice.product.domain.enums.ProductStatus status, @org.jetbrains.annotations.Nullable()
    java.lang.Long categoryId, @org.jetbrains.annotations.Nullable()
    java.lang.String thumbnailImageUrl) {
        return null;
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
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String toString() {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006\u00a8\u0006\u0007"}, d2 = {"Lcom/koosco/catalogservice/product/api/ProductListResponse$Companion;", "", "()V", "from", "Lcom/koosco/catalogservice/product/api/ProductListResponse;", "productInfo", "Lcom/koosco/catalogservice/product/application/result/ProductInfo;", "catalog-service"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.koosco.catalogservice.product.api.ProductListResponse from(@org.jetbrains.annotations.NotNull()
        com.koosco.catalogservice.product.application.result.ProductInfo productInfo) {
            return null;
        }
    }
}