package com.koosco.catalogservice.product.domain;

@org.junit.jupiter.api.DisplayName(value = "ProductSku \ub3c4\uba54\uc778 \ud14c\uc2a4\ud2b8")
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0007\u0018\u00002\u00020\u0001:\u0003\u0005\u0006\u0007B\u0005\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\b"}, d2 = {"Lcom/koosco/catalogservice/product/domain/ProductSkuTest;", "", "()V", "objectMapper", "Lcom/fasterxml/jackson/databind/ObjectMapper;", "CreateTest", "GenerateTest", "OptionValuesFormatTest", "catalog-service_test"})
public final class ProductSkuTest {
    @org.jetbrains.annotations.NotNull()
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper = null;
    
    public ProductSkuTest() {
        super();
    }
    
    @org.junit.jupiter.api.Nested()
    @org.junit.jupiter.api.DisplayName(value = "create \uba54\uc11c\ub4dc\ub294")
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\b\b\b\u0087\u0004\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0012\u0010\u0003\u001a\u00020\u00042\b\b\u0002\u0010\u0005\u001a\u00020\u0006H\u0002J\b\u0010\u0007\u001a\u00020\bH\u0007J\b\u0010\t\u001a\u00020\bH\u0007J\b\u0010\n\u001a\u00020\bH\u0007J\b\u0010\u000b\u001a\u00020\bH\u0007J\b\u0010\f\u001a\u00020\bH\u0007J\b\u0010\r\u001a\u00020\bH\u0007J\b\u0010\u000e\u001a\u00020\bH\u0007J\b\u0010\u000f\u001a\u00020\bH\u0007\u00a8\u0006\u0010"}, d2 = {"Lcom/koosco/catalogservice/product/domain/ProductSkuTest$CreateTest;", "", "(Lcom/koosco/catalogservice/product/domain/ProductSkuTest;)V", "createTestProduct", "Lcom/koosco/catalogservice/product/domain/entity/Product;", "productCode", "", "should create ProductSku with multiple options", "", "should create ProductSku with single option", "should create ProductSku with zero price", "should create ProductSku without options", "should have different SKU ID for different options", "should have same SKU ID for same options", "should set creation time automatically", "should store option values in sorted order", "catalog-service_test"})
    public final class CreateTest {
        
        public CreateTest() {
            super();
        }
        
        private final com.koosco.catalogservice.product.domain.entity.Product createTestProduct(java.lang.String productCode) {
            return null;
        }
    }
    
    @org.junit.jupiter.api.Nested()
    @org.junit.jupiter.api.DisplayName(value = "generate \uba54\uc11c\ub4dc\ub294")
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0006\b\u0087\u0004\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H\u0007J\b\u0010\u0005\u001a\u00020\u0004H\u0007J\b\u0010\u0006\u001a\u00020\u0004H\u0007J\b\u0010\u0007\u001a\u00020\u0004H\u0007J\b\u0010\b\u001a\u00020\u0004H\u0007J\b\u0010\t\u001a\u00020\u0004H\u0007\u00a8\u0006\n"}, d2 = {"Lcom/koosco/catalogservice/product/domain/ProductSkuTest$GenerateTest;", "", "(Lcom/koosco/catalogservice/product/domain/ProductSkuTest;)V", "should generate SKU ID with multiple options", "", "should generate SKU ID with productCode only when no options", "should generate SKU ID with single option", "should generate different SKU IDs for different option combinations", "should generate same SKU ID regardless of option order", "should handle option values with special characters", "catalog-service_test"})
    public final class GenerateTest {
        
        public GenerateTest() {
            super();
        }
    }
    
    @org.junit.jupiter.api.Nested()
    @org.junit.jupiter.api.DisplayName(value = "\uc635\uc158\uac12 JSON \ud3ec\ub9f7\uc740")
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\b\u0087\u0004\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H\u0007J\b\u0010\u0005\u001a\u00020\u0004H\u0007J\b\u0010\u0006\u001a\u00020\u0004H\u0007\u00a8\u0006\u0007"}, d2 = {"Lcom/koosco/catalogservice/product/domain/ProductSkuTest$OptionValuesFormatTest;", "", "(Lcom/koosco/catalogservice/product/domain/ProductSkuTest;)V", "should format as valid JSON", "", "should sort keys alphabetically in JSON", "should store empty options as empty JSON object", "catalog-service_test"})
    public final class OptionValuesFormatTest {
        
        public OptionValuesFormatTest() {
            super();
        }
    }
}