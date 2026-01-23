package com.koosco.userservice.infra.client

import com.koosco.userservice.infra.client.dto.CreateUserRequest
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(name = "auth-service", url = "\${auth-service.url}")
interface AuthClient {

    @PostMapping("/api/auth")
    fun createUser(@RequestBody request: CreateUserRequest)
}
