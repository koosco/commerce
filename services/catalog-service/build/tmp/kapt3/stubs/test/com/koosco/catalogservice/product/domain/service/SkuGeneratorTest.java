package com.koosco.catalogservice.product.domain.service;

@org.junit.jupiter.api.DisplayName(value = "SkuGenerator \uc11c\ube44\uc2a4 \ud14c\uc2a4\ud2b8")
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\b\u0007\u0018\u00002\u00020\u0001:\u0004\u0013\u0014\u0015\u0016B\u0005\u00a2\u0006\u0002\u0010\u0002J2\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0018\u0010\u000b\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u000e0\r0\fH\u0002J\u001c\u0010\u000f\u001a\u00020\u00102\b\b\u0002\u0010\u0011\u001a\u00020\b2\b\b\u0002\u0010\u0012\u001a\u00020\u000eH\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0017"}, d2 = {"Lcom/koosco/catalogservice/product/domain/service/SkuGeneratorTest;", "", "()V", "skuGenerator", "Lcom/koosco/catalogservice/product/domain/service/SkuGenerator;", "createOptionGroup", "Lcom/koosco/catalogservice/product/domain/entity/ProductOptionGroup;", "name", "", "ordering", "", "options", "", "Lkotlin/Pair;", "", "createTestProduct", "Lcom/koosco/catalogservice/product/domain/entity/Product;", "productCode", "price", "GenerateSkusTest", "PriceCalculationTest", "ProductRelationshipTest", "SkuIdGenerationTest", "catalog-service_test"})
public final class SkuGeneratorTest {
    @org.jetbrains.annotations.NotNull()
    private final com.koosco.catalogservice.product.domain.service.SkuGenerator skuGenerator = null;
    
    public SkuGeneratorTest() {
        super();
    }
    
    private final com.koosco.catalogservice.product.domain.entity.Product createTestProduct(java.lang.String productCode, long price) {
        return null;
    }
    
    private final com.koosco.catalogservice.product.domain.entity.ProductOptionGroup createOptionGroup(java.lang.String name, int ordering, java.util.List<kotlin.Pair<java.lang.String, java.lang.Long>> options) {
        return null;
    }
    
    @org.junit.jupiter.api.Nested()
    @org.junit.jupiter.api.DisplayName(value = "generateSkus \uba54\uc11c\ub4dc\ub294")
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0007\b\u0087\u0004\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H\u0007J\b\u0010\u0005\u001a\u00020\u0004H\u0007J\b\u0010\u0006\u001a\u00020\u0004H\u0007J\b\u0010\u0007\u001a\u00020\u0004H\u0007J\b\u0010\b\u001a\u00020\u0004H\u0007J\b\u0010\t\u001a\u00020\u0004H\u0007J\b\u0010\n\u001a\u00020\u0004H\u0007\u00a8\u0006\u000b"}, d2 = {"Lcom/koosco/catalogservice/product/domain/service/SkuGeneratorTest$GenerateSkusTest;", "", "(Lcom/koosco/catalogservice/product/domain/service/SkuGeneratorTest;)V", "should generate SKUs ordered by option group ordering", "", "should generate SKUs ordered by option ordering", "should generate SKUs with single option group", "should generate cartesian product SKUs with three option groups", "should generate cartesian product SKUs with two option groups", "should generate correct option combinations", "should not generate SKUs when no option groups", "catalog-service_test"})
    public final class GenerateSkusTest {
        
        public GenerateSkusTest() {
            super();
        }
    }
    
    @org.junit.jupiter.api.Nested()
    @org.junit.jupiter.api.DisplayName(value = "\uac00\uaca9 \uacc4\uc0b0\uc740")
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0004\b\u0087\u0004\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H\u0007J\b\u0010\u0005\u001a\u00020\u0004H\u0007J\b\u0010\u0006\u001a\u00020\u0004H\u0007J\b\u0010\u0007\u001a\u00020\u0004H\u0007\u00a8\u0006\b"}, d2 = {"Lcom/koosco/catalogservice/product/domain/service/SkuGeneratorTest$PriceCalculationTest;", "", "(Lcom/koosco/catalogservice/product/domain/service/SkuGeneratorTest;)V", "should calculate price with single option additional price", "", "should sum additional prices from multiple options", "should sum additional prices from three options", "should use base price when no additional price", "catalog-service_test"})
    public final class PriceCalculationTest {
        
        public PriceCalculationTest() {
            super();
        }
    }
    
    @org.junit.jupiter.api.Nested()
    @org.junit.jupiter.api.DisplayName(value = "Product\uc640\uc758 \uad00\uacc4\ub294")
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\b\u0087\u0004\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H\u0007J\b\u0010\u0005\u001a\u00020\u0004H\u0007\u00a8\u0006\u0006"}, d2 = {"Lcom/koosco/catalogservice/product/domain/service/SkuGeneratorTest$ProductRelationshipTest;", "", "(Lcom/koosco/catalogservice/product/domain/service/SkuGeneratorTest;)V", "should add generated SKUs to product", "", "should reference product in generated SKUs", "catalog-service_test"})
    public final class ProductRelationshipTest {
        
        public ProductRelationshipTest() {
            super();
        }
    }
    
    @org.junit.jupiter.api.Nested()
    @org.junit.jupiter.api.DisplayName(value = "SKU ID \uc0dd\uc131\uc740")
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\b\u0087\u0004\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H\u0007J\b\u0010\u0005\u001a\u00020\u0004H\u0007\u00a8\u0006\u0006"}, d2 = {"Lcom/koosco/catalogservice/product/domain/service/SkuGeneratorTest$SkuIdGenerationTest;", "", "(Lcom/koosco/catalogservice/product/domain/service/SkuGeneratorTest;)V", "should generate unique SKU IDs", "", "should include productCode in SKU ID", "catalog-service_test"})
    public final class SkuIdGenerationTest {
        
        public SkuIdGenerationTest() {
            super();
        }
    }
}