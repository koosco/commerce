package com.koosco.catalogservice.category.api.controller;

@io.swagger.v3.oas.annotations.tags.Tag(name = "Category", description = "Category management APIs")
@org.springframework.web.bind.annotation.RestController()
@org.springframework.web.bind.annotation.RequestMapping(value = {"/api/catalog/categories"})
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000L\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0000\n\u0002\u0010\t\n\u0002\b\u0003\b\u0017\u0018\u00002\u00020\u0001B%\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u00a2\u0006\u0002\u0010\nJ\u0018\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\f2\b\b\u0001\u0010\u000e\u001a\u00020\u000fH\u0017J\u0018\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00110\f2\b\b\u0001\u0010\u000e\u001a\u00020\u0012H\u0017J%\u0010\u0013\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\r0\u00140\f2\n\b\u0001\u0010\u0015\u001a\u0004\u0018\u00010\u0016H\u0017\u00a2\u0006\u0002\u0010\u0017J\u0014\u0010\u0018\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00110\u00140\fH\u0017R\u000e\u0010\b\u001a\u00020\tX\u0092\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0092\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0092\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0092\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0019"}, d2 = {"Lcom/koosco/catalogservice/category/api/controller/CategoryController;", "", "getCategoryListUseCase", "Lcom/koosco/catalogservice/category/application/usecase/GetCategoryListUseCase;", "getCategoryTreeUseCase", "Lcom/koosco/catalogservice/category/application/usecase/GetCategoryTreeUseCase;", "createCategoryUseCase", "Lcom/koosco/catalogservice/category/application/usecase/CreateCategoryUseCase;", "createCategoryTreeUseCase", "Lcom/koosco/catalogservice/category/application/usecase/CreateCategoryTreeUseCase;", "(Lcom/koosco/catalogservice/category/application/usecase/GetCategoryListUseCase;Lcom/koosco/catalogservice/category/application/usecase/GetCategoryTreeUseCase;Lcom/koosco/catalogservice/category/application/usecase/CreateCategoryUseCase;Lcom/koosco/catalogservice/category/application/usecase/CreateCategoryTreeUseCase;)V", "createCategory", "Lcom/koosco/common/core/response/ApiResponse;", "Lcom/koosco/catalogservice/category/api/CategoryResponse;", "request", "Lcom/koosco/catalogservice/category/api/CategoryCreateRequest;", "createCategoryTree", "Lcom/koosco/catalogservice/category/api/CategoryTreeResponse;", "Lcom/koosco/catalogservice/category/api/CategoryTreeCreateRequest;", "getCategories", "", "parentId", "", "(Ljava/lang/Long;)Lcom/koosco/common/core/response/ApiResponse;", "getCategoryTree", "catalog-service"})
public class CategoryController {
    @org.jetbrains.annotations.NotNull()
    private final com.koosco.catalogservice.category.application.usecase.GetCategoryListUseCase getCategoryListUseCase = null;
    @org.jetbrains.annotations.NotNull()
    private final com.koosco.catalogservice.category.application.usecase.GetCategoryTreeUseCase getCategoryTreeUseCase = null;
    @org.jetbrains.annotations.NotNull()
    private final com.koosco.catalogservice.category.application.usecase.CreateCategoryUseCase createCategoryUseCase = null;
    @org.jetbrains.annotations.NotNull()
    private final com.koosco.catalogservice.category.application.usecase.CreateCategoryTreeUseCase createCategoryTreeUseCase = null;
    
    public CategoryController(@org.jetbrains.annotations.NotNull()
    com.koosco.catalogservice.category.application.usecase.GetCategoryListUseCase getCategoryListUseCase, @org.jetbrains.annotations.NotNull()
    com.koosco.catalogservice.category.application.usecase.GetCategoryTreeUseCase getCategoryTreeUseCase, @org.jetbrains.annotations.NotNull()
    com.koosco.catalogservice.category.application.usecase.CreateCategoryUseCase createCategoryUseCase, @org.jetbrains.annotations.NotNull()
    com.koosco.catalogservice.category.application.usecase.CreateCategoryTreeUseCase createCategoryTreeUseCase) {
        super();
    }
    
    @io.swagger.v3.oas.annotations.Operation(summary = "\uce74\ud14c\uace0\ub9ac \ubaa9\ub85d \uc870\ud68c", description = "\uce74\ud14c\uace0\ub9ac \ubaa9\ub85d\uc744 \uc870\ud68c\ud569\ub2c8\ub2e4. parentId\ub97c \uc785\ub825\ud558\uc9c0 \uc54a\uc744 \uacbd\uc6b0 \ucd5c\uc0c1\uc704 \uce74\ud14c\uace0\ub9ac\ub97c \uc870\ud68c\ud569\ub2c8\ub2e4.")
    @org.springframework.web.bind.annotation.GetMapping()
    @org.jetbrains.annotations.NotNull()
    public com.koosco.common.core.response.ApiResponse<java.util.List<com.koosco.catalogservice.category.api.CategoryResponse>> getCategories(@io.swagger.v3.oas.annotations.Parameter(description = "Parent category ID (null for root categories)")
    @org.springframework.web.bind.annotation.RequestParam(required = false)
    @org.jetbrains.annotations.Nullable()
    java.lang.Long parentId) {
        return null;
    }
    
    @io.swagger.v3.oas.annotations.Operation(summary = "\uce74\ud14c\uace0\ub9ac \ud2b8\ub9ac \uc870\ud68c", description = "\uce74\ud14c\uace0\ub9ac\ub97c \uacc4\uce35 \ud2b8\ub9ac \ud615\ud0dc\ub85c \uc870\ud68c\ud569\ub2c8\ub2e4.")
    @org.springframework.web.bind.annotation.GetMapping(value = {"/tree"})
    @org.jetbrains.annotations.NotNull()
    public com.koosco.common.core.response.ApiResponse<java.util.List<com.koosco.catalogservice.category.api.CategoryTreeResponse>> getCategoryTree() {
        return null;
    }
    
    @io.swagger.v3.oas.annotations.Operation(summary = "\uc0c8\ub85c\uc6b4 \uce74\ud14c\uace0\ub9ac\ub97c \uc0dd\uc131\ud569\ub2c8\ub2e4.", description = "\uc0c8\ub85c\uc6b4 \uce74\ud14c\uace0\ub9ac\ub97c \uc0dd\uc131\ud569\ub2c8\ub2e4. \uad00\ub9ac\uc790\ub9cc \uc0ac\uc6a9 \uac00\ub2a5\ud569\ub2c8\ub2e4.", security = {@io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth")})
    @org.springframework.web.bind.annotation.PostMapping()
    @org.springframework.web.bind.annotation.ResponseStatus(value = org.springframework.http.HttpStatus.CREATED)
    @org.jetbrains.annotations.NotNull()
    public com.koosco.common.core.response.ApiResponse<com.koosco.catalogservice.category.api.CategoryResponse> createCategory(@jakarta.validation.Valid()
    @org.springframework.web.bind.annotation.RequestBody()
    @org.jetbrains.annotations.NotNull()
    com.koosco.catalogservice.category.api.CategoryCreateRequest request) {
        return null;
    }
    
    @io.swagger.v3.oas.annotations.Operation(summary = "\uce74\ud14c\uace0\ub9ac \ud2b8\ub9ac\ub97c \uc0dd\uc131\ud569\ub2c8\ub2e4.", description = "\uacc4\uce35 \uad6c\uc870\ub97c \uac00\uc9c4 \uce74\ud14c\uace0\ub9ac \ud2b8\ub9ac\ub97c \ud55c \ubc88\uc5d0 \uc0dd\uc131\ud569\ub2c8\ub2e4. \uad00\ub9ac\uc790\ub9cc \uc0ac\uc6a9 \uac00\ub2a5\ud569\ub2c8\ub2e4.", security = {@io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth")})
    @org.springframework.web.bind.annotation.PostMapping(value = {"/tree"})
    @org.springframework.web.bind.annotation.ResponseStatus(value = org.springframework.http.HttpStatus.CREATED)
    @org.jetbrains.annotations.NotNull()
    public com.koosco.common.core.response.ApiResponse<com.koosco.catalogservice.category.api.CategoryTreeResponse> createCategoryTree(@jakarta.validation.Valid()
    @org.springframework.web.bind.annotation.RequestBody()
    @org.jetbrains.annotations.NotNull()
    com.koosco.catalogservice.category.api.CategoryTreeCreateRequest request) {
        return null;
    }
}