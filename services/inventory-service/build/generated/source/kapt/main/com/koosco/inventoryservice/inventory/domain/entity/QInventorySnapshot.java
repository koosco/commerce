package com.koosco.inventoryservice.inventory.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QInventorySnapshot is a Querydsl query type for InventorySnapshot
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QInventorySnapshot extends EntityPathBase<InventorySnapshot> {

    private static final long serialVersionUID = 1861516709L;

    public static final QInventorySnapshot inventorySnapshot = new QInventorySnapshot("inventorySnapshot");

    public final NumberPath<Integer> reserved = createNumber("reserved", Integer.class);

    public final StringPath skuId = createString("skuId");

    public final DateTimePath<java.time.LocalDateTime> snapshottedAt = createDateTime("snapshottedAt", java.time.LocalDateTime.class);

    public final NumberPath<Integer> total = createNumber("total", Integer.class);

    public QInventorySnapshot(String variable) {
        super(InventorySnapshot.class, forVariable(variable));
    }

    public QInventorySnapshot(Path<? extends InventorySnapshot> path) {
        super(path.getType(), path.getMetadata());
    }

    public QInventorySnapshot(PathMetadata metadata) {
        super(InventorySnapshot.class, metadata);
    }

}

