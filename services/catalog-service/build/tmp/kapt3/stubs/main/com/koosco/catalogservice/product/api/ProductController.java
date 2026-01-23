package com.koosco.catalogservice.product.api;

@io.swagger.v3.oas.annotations.tags.Tag(name = "Product", description = "Product management APIs")
@org.springframework.web.bind.annotation.RestController()
@org.springframework.web.bind.annotation.RequestMapping(value = {"/api/catalog/products"})
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000t\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0017\u0018\u00002\u00020\u0001B5\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\u000b\u0012\u0006\u0010\f\u001a\u00020\r\u00a2\u0006\u0002\u0010\u000eJ\u0018\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00110\u00102\b\b\u0001\u0010\u0012\u001a\u00020\u0013H\u0017J\u0018\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00010\u00102\b\b\u0001\u0010\u0015\u001a\u00020\u0016H\u0017J.\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00180\u00102\b\b\u0001\u0010\u0015\u001a\u00020\u00162\u0014\b\u0001\u0010\u0019\u001a\u000e\u0012\u0004\u0012\u00020\u001b\u0012\u0004\u0012\u00020\u001b0\u001aH\u0017J\u0018\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u00110\u00102\b\b\u0001\u0010\u0015\u001a\u00020\u0016H\u0017J;\u0010\u001d\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001f0\u001e0\u00102\n\b\u0001\u0010 \u001a\u0004\u0018\u00010\u00162\n\b\u0001\u0010!\u001a\u0004\u0018\u00010\u001b2\b\b\u0001\u0010\"\u001a\u00020#H\u0017\u00a2\u0006\u0002\u0010$J\"\u0010%\u001a\b\u0012\u0004\u0012\u00020\u00010\u00102\b\b\u0001\u0010\u0015\u001a\u00020\u00162\b\b\u0001\u0010\u0012\u001a\u00020&H\u0017R\u000e\u0010\u0006\u001a\u00020\u0007X\u0092\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0092\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0092\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0092\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0092\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0092\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\'"}, d2 = {"Lcom/koosco/catalogservice/product/api/ProductController;", "", "getProductListUseCase", "Lcom/koosco/catalogservice/product/application/usecase/GetProductListUseCase;", "getProductDetailUseCase", "Lcom/koosco/catalogservice/product/application/usecase/GetProductDetailUseCase;", "createProductUseCase", "Lcom/koosco/catalogservice/product/application/usecase/CreateProductUseCase;", "updateProductUseCase", "Lcom/koosco/catalogservice/product/application/usecase/UpdateProductUseCase;", "deleteProductUseCase", "Lcom/koosco/catalogservice/product/application/usecase/DeleteProductUseCase;", "findSkuUseCase", "Lcom/koosco/catalogservice/product/application/usecase/FindSkuUseCase;", "(Lcom/koosco/catalogservice/product/application/usecase/GetProductListUseCase;Lcom/koosco/catalogservice/product/application/usecase/GetProductDetailUseCase;Lcom/koosco/catalogservice/product/application/usecase/CreateProductUseCase;Lcom/koosco/catalogservice/product/application/usecase/UpdateProductUseCase;Lcom/koosco/catalogservice/product/application/usecase/DeleteProductUseCase;Lcom/koosco/catalogservice/product/application/usecase/FindSkuUseCase;)V", "createProduct", "Lcom/koosco/common/core/response/ApiResponse;", "Lcom/koosco/catalogservice/product/api/ProductDetailResponse;", "request", "Lcom/koosco/catalogservice/product/api/ProductCreateRequest;", "deleteProduct", "productId", "", "findSku", "Lcom/koosco/catalogservice/product/api/response/SkuResponse;", "allRequestParams", "", "", "getProduct", "getProducts", "Lorg/springframework/data/domain/Page;", "Lcom/koosco/catalogservice/product/api/ProductListResponse;", "categoryId", "keyword", "pageable", "Lorg/springframework/data/domain/Pageable;", "(Ljava/lang/Long;Ljava/lang/String;Lorg/springframework/data/domain/Pageable;)Lcom/koosco/common/core/response/ApiResponse;", "updateProduct", "Lcom/koosco/catalogservice/product/api/ProductUpdateRequest;", "catalog-service"})
public class ProductController {
    @org.jetbrains.annotations.NotNull()
    private final com.koosco.catalogservice.product.application.usecase.GetProductListUseCase getProductListUseCase = null;
    @org.jetbrains.annotations.NotNull()
    private final com.koosco.catalogservice.product.application.usecase.GetProductDetailUseCase getProductDetailUseCase = null;
    @org.jetbrains.annotations.NotNull()
    private final com.koosco.catalogservice.product.application.usecase.CreateProductUseCase createProductUseCase = null;
    @org.jetbrains.annotations.NotNull()
    private final com.koosco.catalogservice.product.application.usecase.UpdateProductUseCase updateProductUseCase = null;
    @org.jetbrains.annotations.NotNull()
    private final com.koosco.catalogservice.product.application.usecase.DeleteProductUseCase deleteProductUseCase = null;
    @org.jetbrains.annotations.NotNull()
    private final com.koosco.catalogservice.product.application.usecase.FindSkuUseCase findSkuUseCase = null;
    
    public ProductController(@org.jetbrains.annotations.NotNull()
    com.koosco.catalogservice.product.application.usecase.GetProductListUseCase getProductListUseCase, @org.jetbrains.annotations.NotNull()
    com.koosco.catalogservice.product.application.usecase.GetProductDetailUseCase getProductDetailUseCase, @org.jetbrains.annotations.NotNull()
    com.koosco.catalogservice.product.application.usecase.CreateProductUseCase createProductUseCase, @org.jetbrains.annotations.NotNull()
    com.koosco.catalogservice.product.application.usecase.UpdateProductUseCase updateProductUseCase, @org.jetbrains.annotations.NotNull()
    com.koosco.catalogservice.product.application.usecase.DeleteProductUseCase deleteProductUseCase, @org.jetbrains.annotations.NotNull()
    com.koosco.catalogservice.product.application.usecase.FindSkuUseCase findSkuUseCase) {
        super();
    }
    
    @io.swagger.v3.oas.annotations.Operation(summary = "\uc0c1\ud488 \ub9ac\uc2a4\ud2b8\ub97c \uc870\ud68c\ud569\ub2c8\ub2e4.", description = "\ud544\ud130\ub9c1 \uc870\uac74\uc5d0 \ub530\ub77c \uc0c1\ud488\uc744 \ud398\uc774\uc9d5\ucc98\ub9ac\ud558\uc5ec \uc870\ud68c\ud569\ub2c8\ub2e4.")
    @org.springframework.web.bind.annotation.GetMapping()
    @org.jetbrains.annotations.NotNull()
    public com.koosco.common.core.response.ApiResponse<org.springframework.data.domain.Page<com.koosco.catalogservice.product.api.ProductListResponse>> getProducts(@io.swagger.v3.oas.annotations.Parameter(description = "\uce74\ud14c\uace0\ub9ac ID")
    @org.springframework.web.bind.annotation.RequestParam(required = false)
    @org.jetbrains.annotations.Nullable()
    java.lang.Long categoryId, @io.swagger.v3.oas.annotations.Parameter(description = "\uc774\ub984 \ub610\ub294 \uc0c1\ud488 \uc124\uba85")
    @org.springframework.web.bind.annotation.RequestParam(required = false)
    @org.jetbrains.annotations.Nullable()
    java.lang.String keyword, @io.swagger.v3.oas.annotations.Parameter(description = "\ud398\uc774\uc9d5 \ud30c\ub77c\ubbf8\ud130 (page, size, sort)")
    @org.springframework.data.web.PageableDefault(size = 20)
    @org.jetbrains.annotations.NotNull()
    org.springframework.data.domain.Pageable pageable) {
        return null;
    }
    
    @io.swagger.v3.oas.annotations.Operation(summary = "\uc0c1\ud488 \uc0c1\uc138\ub97c \uc870\ud68c\ud569\ub2c8\ub2e4.", description = "\uc635\uc158\uc744 \ud3ec\ud568\ud558\uc5ec \uc0c1\ud488\uc744 \uc870\ud68c\ud569\ub2c8\ub2e4.")
    @org.springframework.web.bind.annotation.GetMapping(value = {"/{productId}"})
    @org.jetbrains.annotations.NotNull()
    public com.koosco.common.core.response.ApiResponse<com.koosco.catalogservice.product.api.ProductDetailResponse> getProduct(@io.swagger.v3.oas.annotations.Parameter(description = "Product ID")
    @org.springframework.web.bind.annotation.PathVariable()
    long productId) {
        return null;
    }
    
    @io.swagger.v3.oas.annotations.Operation(summary = "\uc635\uc158 \uc870\ud569\uc73c\ub85c SKU\ub97c \uc870\ud68c\ud569\ub2c8\ub2e4.", description = "\uc0ac\uc6a9\uc790\uac00 \uc120\ud0dd\ud55c \uc635\uc158 \uc870\ud569\uc5d0 \ud574\ub2f9\ud558\ub294 SKU \uc815\ubcf4(\uac00\uaca9, \uc7ac\uace0 \ub4f1)\ub97c \uc870\ud68c\ud569\ub2c8\ub2e4. \uc608\uc2dc: GET /api/catalog/products/2/skus?Volume=100ml&Package=Single")
    @org.springframework.web.bind.annotation.GetMapping(value = {"/{productId}/skus"})
    @org.jetbrains.annotations.NotNull()
    public com.koosco.common.core.response.ApiResponse<com.koosco.catalogservice.product.api.response.SkuResponse> findSku(@io.swagger.v3.oas.annotations.Parameter(description = "Product ID")
    @org.springframework.web.bind.annotation.PathVariable()
    long productId, @io.swagger.v3.oas.annotations.Parameter(description = "\uc635\uc158 \uc870\ud569 (\uc608: Volume=100ml&Package=Single)")
    @org.springframework.web.bind.annotation.RequestParam()
    @org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.String, java.lang.String> allRequestParams) {
        return null;
    }
    
    @io.swagger.v3.oas.annotations.Operation(summary = "\uc0c8\ub85c\uc6b4 \uc0c1\ud488\uc744 \ucd94\uac00\ud569\ub2c8\ub2e4.", description = "\uc0c1\ud488 \uc635\uc158\uacfc \ud568\uaed8 \uc0c1\ud488\uc744 \uc0dd\uc131\ud569\ub2c8\ub2e4. \ud310\ub9e4\uc790\ub9cc \ub4f1\ub85d\uc774 \uac00\ub2a5\ud569\ub2c8\ub2e4.", security = {@io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth")})
    @org.springframework.web.bind.annotation.PostMapping()
    @org.springframework.web.bind.annotation.ResponseStatus(value = org.springframework.http.HttpStatus.CREATED)
    @org.jetbrains.annotations.NotNull()
    public com.koosco.common.core.response.ApiResponse<com.koosco.catalogservice.product.api.ProductDetailResponse> createProduct(@jakarta.validation.Valid()
    @org.springframework.web.bind.annotation.RequestBody()
    @org.jetbrains.annotations.NotNull()
    com.koosco.catalogservice.product.api.ProductCreateRequest request) {
        return null;
    }
    
    @io.swagger.v3.oas.annotations.Operation(summary = "\uc0c1\ud488 \uc815\ubcf4\ub97c \uc5c5\ub370\uc774\ud2b8\ud569\ub2c8\ub2e4.", description = "\uc0c1\ud488 \uc815\ubcf4\ub97c \uc5c5\ub370\uc774\ud2b8\ud569\ub2c8\ub2e4. \ud310\ub9e4\uc790\ub9cc \uc218\uc815\uc774 \uac00\ub2a5\ud569\ub2c8\ub2e4.", security = {@io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth")})
    @org.springframework.web.bind.annotation.PutMapping(value = {"/{productId}"})
    @org.jetbrains.annotations.NotNull()
    public com.koosco.common.core.response.ApiResponse<java.lang.Object> updateProduct(@io.swagger.v3.oas.annotations.Parameter(description = "Product ID")
    @org.springframework.web.bind.annotation.PathVariable()
    long productId, @jakarta.validation.Valid()
    @org.springframework.web.bind.annotation.RequestBody()
    @org.jetbrains.annotations.NotNull()
    com.koosco.catalogservice.product.api.ProductUpdateRequest request) {
        return null;
    }
    
    @io.swagger.v3.oas.annotations.Operation(summary = "\uc0c1\ud488\uc744 \uc0ad\uc81c\ud569\ub2c8\ub2e4.", description = "\uc0c1\ud488\uc744 \uc0ad\uc81c\ud569\ub2c8\ub2e4. \ud310\ub9e4\uc790\ub9cc \uc0ad\uc81c\uac00 \uac00\ub2a5\ud569\ub2c8\ub2e4.", security = {@io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth")})
    @org.springframework.web.bind.annotation.DeleteMapping(value = {"/{productId}"})
    @org.springframework.web.bind.annotation.ResponseStatus(value = org.springframework.http.HttpStatus.NO_CONTENT)
    @org.jetbrains.annotations.NotNull()
    public com.koosco.common.core.response.ApiResponse<java.lang.Object> deleteProduct(@io.swagger.v3.oas.annotations.Parameter(description = "Product ID")
    @org.springframework.web.bind.annotation.PathVariable()
    long productId) {
        return null;
    }
}