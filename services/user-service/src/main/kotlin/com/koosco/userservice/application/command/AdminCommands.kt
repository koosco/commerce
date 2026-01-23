package com.koosco.userservice.application.command

data class ForceDeleteCommand(val userId: Long)

data class ForceUpdateCommand(val userId: Long, val name: String?, val phone: String?) {
    companion object {
        fun of(userId: Long, name: String?, phone: String?): ForceUpdateCommand = ForceUpdateCommand(
            userId,
            name,
            phone,
        )
    }
}
