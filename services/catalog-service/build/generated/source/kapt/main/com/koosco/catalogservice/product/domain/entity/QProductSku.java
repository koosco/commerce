package com.koosco.catalogservice.product.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProductSku is a Querydsl query type for ProductSku
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProductSku extends EntityPathBase<ProductSku> {

    private static final long serialVersionUID = -710957261L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProductSku productSku = new QProductSku("productSku");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath optionValues = createString("optionValues");

    public final NumberPath<Long> price = createNumber("price", Long.class);

    public final QProduct product;

    public final StringPath skuId = createString("skuId");

    public QProductSku(String variable) {
        this(ProductSku.class, forVariable(variable), INITS);
    }

    public QProductSku(Path<? extends ProductSku> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProductSku(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProductSku(PathMetadata metadata, PathInits inits) {
        this(ProductSku.class, metadata, inits);
    }

    public QProductSku(Class<? extends ProductSku> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.product = inits.isInitialized("product") ? new QProduct(forProperty("product")) : null;
    }

}

