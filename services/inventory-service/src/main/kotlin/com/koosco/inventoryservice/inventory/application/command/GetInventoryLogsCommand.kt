package com.koosco.inventoryservice.inventory.application.command

import java.time.LocalDateTime

data class GetInventoryLogsCommand(val skuId: String, val from: LocalDateTime?, val to: LocalDateTime?)
