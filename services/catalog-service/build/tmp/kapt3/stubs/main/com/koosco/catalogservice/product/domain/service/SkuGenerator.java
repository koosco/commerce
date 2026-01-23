package com.koosco.catalogservice.product.domain.service;

@org.springframework.stereotype.Service()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0017\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J.\u0010\u0003\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\u00050\u00040\u0004\"\u0004\b\u0000\u0010\u00052\u0012\u0010\u0006\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\u00050\u00040\u0004H\u0012J\u0010\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0016\u00a8\u0006\u000b"}, d2 = {"Lcom/koosco/catalogservice/product/domain/service/SkuGenerator;", "", "()V", "cartesianProduct", "", "T", "lists", "generateSkus", "", "product", "Lcom/koosco/catalogservice/product/domain/entity/Product;", "catalog-service"})
public class SkuGenerator {
    
    public SkuGenerator() {
        super();
    }
    
    /**
     * 옵션 조합을 생성하여 모든 가능한 SKU를 생성합니다.
     * 예: 색상(빨강, 파랑) x 사이즈(S, M) -> 4개의 SKU
     */
    public void generateSkus(@org.jetbrains.annotations.NotNull()
    com.koosco.catalogservice.product.domain.entity.Product product) {
    }
    
    private <T extends java.lang.Object>java.util.List<java.util.List<T>> cartesianProduct(java.util.List<? extends java.util.List<? extends T>> lists) {
        return null;
    }
}