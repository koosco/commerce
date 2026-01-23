package com.koosco.paymentservice.infra.toss

import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.web.client.RestClient

/**
 * fileName       : TossPaymentTest
 * author         : koo
 * date           : 2025. 12. 24. 오후 5:34
 * description    :
 */
class TossPaymentTest {

    @Test
    fun testToss() {
        val client = RestClient.create()

        val requestBody = """
            {
                "paymentKey": "5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1",
                "orderId": "a4CWyWY5m89PNh7xJwhk1",
                "amount": 1000
            }
        """.trimIndent()

        val response = client.post()
            .uri("https://api.tosspayments.com/v1/payments/confirm")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Basic dGVzdF9za195TDBxWjRHMVZPNWVtTDF5T216TThvV2IyTVFZOg==")
            .body(requestBody)
            .retrieve()
            .toEntity(String::class.java)

        println(response)
    }
}
