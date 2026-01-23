package com.koosco.catalogservice.product.domain.service;

@org.springframework.stereotype.Service()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0017\u0018\u0000 \t2\u00020\u0001:\u0001\tB\u0005\u00a2\u0006\u0002\u0010\u0002J\u0016\u0010\u0003\u001a\u00020\u00042\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006H\u0016J\u0016\u0010\b\u001a\u00020\u00042\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006H\u0016\u00a8\u0006\n"}, d2 = {"Lcom/koosco/catalogservice/product/domain/service/ProductValidator;", "", "()V", "validateOptionGroupStructure", "", "optionGroupSpecs", "", "Lcom/koosco/catalogservice/product/domain/vo/OptionGroupCreateSpec;", "validateSkuCount", "Companion", "catalog-service"})
public class ProductValidator {
    private static final int MAX_OPTION_GROUPS = 5;
    private static final int MAX_OPTIONS_PER_GROUP = 20;
    private static final int MAX_SKU_COUNT = 500;
    private static final int RECOMMENDED_MAX_SKU_COUNT = 100;
    @org.jetbrains.annotations.NotNull()
    public static final com.koosco.catalogservice.product.domain.service.ProductValidator.Companion Companion = null;
    
    public ProductValidator() {
        super();
    }
    
    /**
     * 상품 생성 시 SKU 개수 검증
     *
     * @throws IllegalArgumentException SKU 개수가 제한을 초과하는 경우
     */
    public void validateSkuCount(@org.jetbrains.annotations.NotNull()
    java.util.List<com.koosco.catalogservice.product.domain.vo.OptionGroupCreateSpec> optionGroupSpecs) {
    }
    
    /**
     * 옵션 그룹 구조 검증 (일반적인 실수 방지)
     *
     * 예: "RED", "BLUE" 등이 옵션 그룹 이름으로 사용되는 경우
     */
    public void validateOptionGroupStructure(@org.jetbrains.annotations.NotNull()
    java.util.List<com.koosco.catalogservice.product.domain.vo.OptionGroupCreateSpec> optionGroupSpecs) {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0004\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\b"}, d2 = {"Lcom/koosco/catalogservice/product/domain/service/ProductValidator$Companion;", "", "()V", "MAX_OPTIONS_PER_GROUP", "", "MAX_OPTION_GROUPS", "MAX_SKU_COUNT", "RECOMMENDED_MAX_SKU_COUNT", "catalog-service"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}