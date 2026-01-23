package com.koosco.inventoryservice.inventory.domain.vo;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QStock is a Querydsl query type for Stock
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QStock extends BeanPath<Stock> {

    private static final long serialVersionUID = -944994479L;

    public static final QStock stock = new QStock("stock");

    public final NumberPath<Integer> reserved = createNumber("reserved", Integer.class);

    public final NumberPath<Integer> total = createNumber("total", Integer.class);

    public QStock(String variable) {
        super(Stock.class, forVariable(variable));
    }

    public QStock(Path<? extends Stock> path) {
        super(path.getType(), path.getMetadata());
    }

    public QStock(PathMetadata metadata) {
        super(Stock.class, metadata);
    }

}

