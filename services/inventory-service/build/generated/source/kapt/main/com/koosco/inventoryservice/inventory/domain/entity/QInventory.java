package com.koosco.inventoryservice.inventory.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QInventory is a Querydsl query type for Inventory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QInventory extends EntityPathBase<Inventory> {

    private static final long serialVersionUID = -1923377983L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QInventory inventory = new QInventory("inventory");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath skuId = createString("skuId");

    public final com.koosco.inventoryservice.inventory.domain.vo.QStock stock;

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QInventory(String variable) {
        this(Inventory.class, forVariable(variable), INITS);
    }

    public QInventory(Path<? extends Inventory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QInventory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QInventory(PathMetadata metadata, PathInits inits) {
        this(Inventory.class, metadata, inits);
    }

    public QInventory(Class<? extends Inventory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.stock = inits.isInitialized("stock") ? new com.koosco.inventoryservice.inventory.domain.vo.QStock(forProperty("stock")) : null;
    }

}

