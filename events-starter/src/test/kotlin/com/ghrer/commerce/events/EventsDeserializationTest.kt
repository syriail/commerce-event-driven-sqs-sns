package com.ghrer.commerce.events

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.ghrer.commerce.events.orders.model.OrderCreatedEvent
import com.ghrer.commerce.events.orders.model.OrderEvent
import com.ghrer.commerce.events.util.FileUtil
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class EventsDeserializationTest {

    private val objectMapper = ObjectMapper()
        .registerKotlinModule()
        .registerModule(JavaTimeModule())

    @Test
    fun `should deserialize OrderCreatedEvent correctly`() {
        val eventPayload = FileUtil.readJsonFileAsString(
            "/events/order-created-event.json"
        )
        eventPayload?.let { it ->
            val event = objectMapper.readValue(it, OrderEvent::class.java)
            Assertions.assertThat(event).isInstanceOf(OrderCreatedEvent::class.java)
        }
    }
}
