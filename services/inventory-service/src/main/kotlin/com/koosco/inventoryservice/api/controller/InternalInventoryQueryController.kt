package com.koosco.inventoryservice.api.controller

import com.koosco.inventoryservice.application.command.GetInventoriesCommand
import com.koosco.inventoryservice.application.usecase.GetInventoryUseCase
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/internal/inventories")
class InternalInventoryQueryController(private val getInventoryUseCase: GetInventoryUseCase) {

    @GetMapping("/availability")
    fun getAvailability(@RequestParam skuIds: List<String>): Map<String, Boolean> {
        val results = getInventoryUseCase.execute(GetInventoriesCommand(skuIds = skuIds))
        return results.associate { it.skuId to (it.availableStock > 0) }
    }
}
