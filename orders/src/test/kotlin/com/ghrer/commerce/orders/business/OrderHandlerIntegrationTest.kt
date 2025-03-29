package com.ghrer.commerce.orders.business

import com.ghrer.commerce.orders.BaseIntegrationTest
import com.ghrer.commerce.orders.fixture.OrderFixture
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import reactor.test.StepVerifier

class OrderHandlerIntegrationTest : BaseIntegrationTest() {

    @Autowired
    private lateinit var orderHandler: OrderHandler

    @Test
    fun `should create an order and publish OrderCreatedEvent`() {
        StepVerifier.create(
            orderHandler.createOrder(
                OrderFixture.getSampleCreateOrderRequest()
            )
        ).consumeNextWith {
            Assertions.assertThat(it).isNotNull
        }
            .verifyComplete()
    }
}
