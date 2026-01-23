package com.koosco.inventoryservice.inventory.domain.exception;

/**
 * fileName       : BusinessException
 * author         : koo
 * date           : 2025. 12. 19. 오후 3:37
 * description    : 비즈니스 규칙 위반을 나타내는 예외
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0003\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\b&\u0018\u00002\u00020\u0001B5\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u0012\u0010\b\u0002\u0010\b\u001a\n\u0012\u0004\u0012\u00020\n\u0018\u00010\t\u00a2\u0006\u0002\u0010\u000b\u00a8\u0006\f"}, d2 = {"Lcom/koosco/inventoryservice/inventory/domain/exception/BusinessException;", "Lcom/koosco/common/core/exception/BaseException;", "errorCode", "Lcom/koosco/common/core/error/ErrorCode;", "message", "", "cause", "", "fieldErrors", "", "Lcom/koosco/common/core/error/ApiError$FieldError;", "(Lcom/koosco/common/core/error/ErrorCode;Ljava/lang/String;Ljava/lang/Throwable;Ljava/util/List;)V", "inventory-service"})
public abstract class BusinessException extends com.koosco.common.core.exception.BaseException {
    
    public BusinessException(@org.jetbrains.annotations.NotNull()
    com.koosco.common.core.error.ErrorCode errorCode, @org.jetbrains.annotations.NotNull()
    java.lang.String message, @org.jetbrains.annotations.Nullable()
    java.lang.Throwable cause, @org.jetbrains.annotations.Nullable()
    java.util.List<com.koosco.common.core.error.ApiError.FieldError> fieldErrors) {
        super(null, null, null, null);
    }
}