package com.koosco.catalogservice.product.api;

/**
 * Create Request
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u001a\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0086\b\u0018\u00002\u00020\u0001:\u000201BW\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\b\b\u0002\u0010\u0007\u001a\u00020\b\u0012\b\u0010\t\u001a\u0004\u0018\u00010\u0006\u0012\b\u0010\n\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u000b\u001a\u0004\u0018\u00010\u0003\u0012\u000e\b\u0002\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000e0\r\u00a2\u0006\u0002\u0010\u000fJ\t\u0010\u001e\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010\u001f\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\t\u0010 \u001a\u00020\u0006H\u00c6\u0003J\t\u0010!\u001a\u00020\bH\u00c6\u0003J\u0010\u0010\"\u001a\u0004\u0018\u00010\u0006H\u00c6\u0003\u00a2\u0006\u0002\u0010\u0013J\u000b\u0010#\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000b\u0010$\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000f\u0010%\u001a\b\u0012\u0004\u0012\u00020\u000e0\rH\u00c6\u0003Jl\u0010&\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\b2\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\u00062\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\u00032\u000e\b\u0002\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000e0\rH\u00c6\u0001\u00a2\u0006\u0002\u0010\'J\u0013\u0010(\u001a\u00020)2\b\u0010*\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010+\u001a\u00020,H\u00d6\u0001J\u0006\u0010-\u001a\u00020.J\t\u0010/\u001a\u00020\u0003H\u00d6\u0001R\u0013\u0010\u000b\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0015\u0010\t\u001a\u0004\u0018\u00010\u0006\u00a2\u0006\n\n\u0002\u0010\u0014\u001a\u0004\b\u0012\u0010\u0013R\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0011R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0011R\u001c\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000e0\r8\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u0016\u0010\u0005\u001a\u00020\u00068\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001aR\u0011\u0010\u0007\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u001cR\u0013\u0010\n\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u0011\u00a8\u00062"}, d2 = {"Lcom/koosco/catalogservice/product/api/ProductCreateRequest;", "", "name", "", "description", "price", "", "status", "Lcom/koosco/catalogservice/product/domain/enums/ProductStatus;", "categoryId", "thumbnailImageUrl", "brand", "optionGroups", "", "Lcom/koosco/catalogservice/product/api/ProductCreateRequest$ProductOptionGroup;", "(Ljava/lang/String;Ljava/lang/String;JLcom/koosco/catalogservice/product/domain/enums/ProductStatus;Ljava/lang/Long;Ljava/lang/String;Ljava/lang/String;Ljava/util/List;)V", "getBrand", "()Ljava/lang/String;", "getCategoryId", "()Ljava/lang/Long;", "Ljava/lang/Long;", "getDescription", "getName", "getOptionGroups", "()Ljava/util/List;", "getPrice", "()J", "getStatus", "()Lcom/koosco/catalogservice/product/domain/enums/ProductStatus;", "getThumbnailImageUrl", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "copy", "(Ljava/lang/String;Ljava/lang/String;JLcom/koosco/catalogservice/product/domain/enums/ProductStatus;Ljava/lang/Long;Ljava/lang/String;Ljava/lang/String;Ljava/util/List;)Lcom/koosco/catalogservice/product/api/ProductCreateRequest;", "equals", "", "other", "hashCode", "", "toCommand", "Lcom/koosco/catalogservice/product/application/command/CreateProductCommand;", "toString", "ProductOption", "ProductOptionGroup", "catalog-service"})
public final class ProductCreateRequest {
    @jakarta.validation.constraints.NotBlank(message = "Product name is required")
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String name = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String description = null;
    @jakarta.validation.constraints.NotNull(message = "Price is required")
    @jakarta.validation.constraints.Min(value = 0L, message = "Price must be non-negative")
    private final long price = 0L;
    @org.jetbrains.annotations.NotNull()
    private final com.koosco.catalogservice.product.domain.enums.ProductStatus status = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Long categoryId = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String thumbnailImageUrl = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String brand = null;
    @jakarta.validation.Valid()
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.koosco.catalogservice.product.api.ProductCreateRequest.ProductOptionGroup> optionGroups = null;
    
    public ProductCreateRequest(@org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.Nullable()
    java.lang.String description, long price, @org.jetbrains.annotations.NotNull()
    com.koosco.catalogservice.product.domain.enums.ProductStatus status, @org.jetbrains.annotations.Nullable()
    java.lang.Long categoryId, @org.jetbrains.annotations.Nullable()
    java.lang.String thumbnailImageUrl, @org.jetbrains.annotations.Nullable()
    java.lang.String brand, @org.jetbrains.annotations.NotNull()
    java.util.List<com.koosco.catalogservice.product.api.ProductCreateRequest.ProductOptionGroup> optionGroups) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getName() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getDescription() {
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
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getBrand() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.koosco.catalogservice.product.api.ProductCreateRequest.ProductOptionGroup> getOptionGroups() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.koosco.catalogservice.product.application.command.CreateProductCommand toCommand() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component1() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
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
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component7() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.koosco.catalogservice.product.api.ProductCreateRequest.ProductOptionGroup> component8() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.koosco.catalogservice.product.api.ProductCreateRequest copy(@org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.Nullable()
    java.lang.String description, long price, @org.jetbrains.annotations.NotNull()
    com.koosco.catalogservice.product.domain.enums.ProductStatus status, @org.jetbrains.annotations.Nullable()
    java.lang.Long categoryId, @org.jetbrains.annotations.Nullable()
    java.lang.String thumbnailImageUrl, @org.jetbrains.annotations.Nullable()
    java.lang.String brand, @org.jetbrains.annotations.NotNull()
    java.util.List<com.koosco.catalogservice.product.api.ProductCreateRequest.ProductOptionGroup> optionGroups) {
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
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\b\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B!\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bJ\t\u0010\u000f\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0010\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0011\u001a\u00020\u0007H\u00c6\u0003J\'\u0010\u0012\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u0007H\u00c6\u0001J\u0013\u0010\u0013\u001a\u00020\u00142\b\u0010\u0015\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0016\u001a\u00020\u0007H\u00d6\u0001J\u0006\u0010\u0017\u001a\u00020\u0018J\t\u0010\u0019\u001a\u00020\u0003H\u00d6\u0001R\u0016\u0010\u0004\u001a\u00020\u00058\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000e\u00a8\u0006\u001a"}, d2 = {"Lcom/koosco/catalogservice/product/api/ProductCreateRequest$ProductOption;", "", "name", "", "additionalPrice", "", "ordering", "", "(Ljava/lang/String;JI)V", "getAdditionalPrice", "()J", "getName", "()Ljava/lang/String;", "getOrdering", "()I", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "toCommand", "Lcom/koosco/catalogservice/product/application/command/CreateProductCommand$ProductOption;", "toString", "catalog-service"})
    public static final class ProductOption {
        @jakarta.validation.constraints.NotBlank(message = "Option name is required")
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String name = null;
        @jakarta.validation.constraints.Min(value = 0L, message = "Additional price must be non-negative")
        private final long additionalPrice = 0L;
        private final int ordering = 0;
        
        public ProductOption(@org.jetbrains.annotations.NotNull()
        java.lang.String name, long additionalPrice, int ordering) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getName() {
            return null;
        }
        
        public final long getAdditionalPrice() {
            return 0L;
        }
        
        public final int getOrdering() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.koosco.catalogservice.product.application.command.CreateProductCommand.ProductOption toCommand() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component1() {
            return null;
        }
        
        public final long component2() {
            return 0L;
        }
        
        public final int component3() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.koosco.catalogservice.product.api.ProductCreateRequest.ProductOption copy(@org.jetbrains.annotations.NotNull()
        java.lang.String name, long additionalPrice, int ordering) {
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
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B\'\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\u000e\b\u0002\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007\u00a2\u0006\u0002\u0010\tJ\t\u0010\u0010\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0011\u001a\u00020\u0005H\u00c6\u0003J\u000f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\b0\u0007H\u00c6\u0003J-\u0010\u0013\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\u000e\b\u0002\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007H\u00c6\u0001J\u0013\u0010\u0014\u001a\u00020\u00152\b\u0010\u0016\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0017\u001a\u00020\u0005H\u00d6\u0001J\u0006\u0010\u0018\u001a\u00020\u0019J\t\u0010\u001a\u001a\u00020\u0003H\u00d6\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u001c\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u00078\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000f\u00a8\u0006\u001b"}, d2 = {"Lcom/koosco/catalogservice/product/api/ProductCreateRequest$ProductOptionGroup;", "", "name", "", "ordering", "", "options", "", "Lcom/koosco/catalogservice/product/api/ProductCreateRequest$ProductOption;", "(Ljava/lang/String;ILjava/util/List;)V", "getName", "()Ljava/lang/String;", "getOptions", "()Ljava/util/List;", "getOrdering", "()I", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "toCommand", "Lcom/koosco/catalogservice/product/application/command/CreateProductCommand$ProductOptionGroup;", "toString", "catalog-service"})
    public static final class ProductOptionGroup {
        @jakarta.validation.constraints.NotBlank(message = "Option group name is required")
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String name = null;
        private final int ordering = 0;
        @jakarta.validation.Valid()
        @org.jetbrains.annotations.NotNull()
        private final java.util.List<com.koosco.catalogservice.product.api.ProductCreateRequest.ProductOption> options = null;
        
        public ProductOptionGroup(@org.jetbrains.annotations.NotNull()
        java.lang.String name, int ordering, @org.jetbrains.annotations.NotNull()
        java.util.List<com.koosco.catalogservice.product.api.ProductCreateRequest.ProductOption> options) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getName() {
            return null;
        }
        
        public final int getOrdering() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<com.koosco.catalogservice.product.api.ProductCreateRequest.ProductOption> getOptions() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.koosco.catalogservice.product.application.command.CreateProductCommand.ProductOptionGroup toCommand() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component1() {
            return null;
        }
        
        public final int component2() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<com.koosco.catalogservice.product.api.ProductCreateRequest.ProductOption> component3() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.koosco.catalogservice.product.api.ProductCreateRequest.ProductOptionGroup copy(@org.jetbrains.annotations.NotNull()
        java.lang.String name, int ordering, @org.jetbrains.annotations.NotNull()
        java.util.List<com.koosco.catalogservice.product.api.ProductCreateRequest.ProductOption> options) {
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
    }
}