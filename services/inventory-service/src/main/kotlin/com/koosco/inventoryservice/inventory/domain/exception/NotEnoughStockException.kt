package com.koosco.inventoryservice.inventory.domain.exception

import com.koosco.inventoryservice.common.InventoryErrorCode

/**
 * fileName       : NotEnoughStockException
 * author         : koo
 * date           : 2025. 12. 19. 오후 1:54
 * description    :
 */
class NotEnoughStockException(
    message: String = "Not enough stock available",
    val skuId: String? = null,
    val requestedQuantity: Int? = null,
    val availableQuantity: Int? = null,
) : BusinessException(
    InventoryErrorCode.NOT_ENOUGH_STOCK,
    message,
)
