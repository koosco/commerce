package com.koosco.catalogservice.product.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProduct is a Querydsl query type for Product
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProduct extends EntityPathBase<Product> {

    private static final long serialVersionUID = 1545189770L;

    public static final QProduct product = new QProduct("product");

    public final StringPath brand = createString("brand");

    public final NumberPath<Long> categoryId = createNumber("categoryId", Long.class);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath description = createString("description");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final ListPath<ProductOptionGroup, QProductOptionGroup> optionGroups = this.<ProductOptionGroup, QProductOptionGroup>createList("optionGroups", ProductOptionGroup.class, QProductOptionGroup.class, PathInits.DIRECT2);

    public final NumberPath<Long> price = createNumber("price", Long.class);

    public final StringPath productCode = createString("productCode");

    public final ListPath<ProductSku, QProductSku> skus = this.<ProductSku, QProductSku>createList("skus", ProductSku.class, QProductSku.class, PathInits.DIRECT2);

    public final EnumPath<com.koosco.catalogservice.product.domain.enums.ProductStatus> status = createEnum("status", com.koosco.catalogservice.product.domain.enums.ProductStatus.class);

    public final StringPath thumbnailImageUrl = createString("thumbnailImageUrl");

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QProduct(String variable) {
        super(Product.class, forVariable(variable));
    }

    public QProduct(Path<? extends Product> path) {
        super(path.getType(), path.getMetadata());
    }

    public QProduct(PathMetadata metadata) {
        super(Product.class, metadata);
    }

}

