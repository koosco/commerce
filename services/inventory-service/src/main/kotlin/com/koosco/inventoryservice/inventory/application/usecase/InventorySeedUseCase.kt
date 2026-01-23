package com.koosco.inventoryservice.inventory.application.usecase

import com.koosco.common.core.annotation.UseCase
import com.koosco.inventoryservice.inventory.application.port.InventorySeedPort
import org.springframework.context.annotation.Profile

/**
 * fileName       : InventorySeedUseCase
 * author         : koo
 * date           : 2025. 12. 26. 오전 4:59
 * description    : 더미 데이터 초기화 usecase
 */
@Profile("local")
@UseCase
class InventorySeedUseCase(private val inventorySeedPort: InventorySeedPort) {

    fun init() {
        inventorySeedPort.initStock("SKU-001", 10_000)
        inventorySeedPort.initStock("SKU-002", 10_000)
    }

    fun clear() {
        inventorySeedPort.clear()
        inventorySeedPort.clear()
    }
}
