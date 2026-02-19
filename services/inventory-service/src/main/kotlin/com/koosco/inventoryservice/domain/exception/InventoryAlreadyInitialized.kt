package com.koosco.inventoryservice.domain.exception

import com.koosco.inventoryservice.common.InventoryErrorCode

/**
 * fileName       : InventoryAlreadyInitialized
 * author         : koo
 * date           : 2025. 12. 19. 오후 3:37
 * description    :
 */
class InventoryAlreadyInitialized(message: String = "Inventory already initialized") :
    BusinessException(
        InventoryErrorCode.INVENTORY_ALREADY_EXISTS,
        message,
    )
