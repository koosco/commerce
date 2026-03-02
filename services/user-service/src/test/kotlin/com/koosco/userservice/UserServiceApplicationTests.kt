package com.koosco.userservice

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@Disabled("Requires database connection - run in integration test phase")
class UserServiceApplicationTests {

    @Test
    fun contextLoads() {
    }
}
