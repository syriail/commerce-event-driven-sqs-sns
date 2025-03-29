package com.ghrer.commerce.orders.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.ghrer.commerce.events.orders.model.OrderEvent
import com.ghrer.commerce.orders.event.handler.EventHandlerProxy
import io.awspring.cloud.sqs.annotation.SqsListener
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("!test")
class SqsListener(
    private val objectMapper: ObjectMapper,
    private val eventHandlerProxy: EventHandlerProxy
) {

    @SqsListener("\${aws-resources.ordersTopicOrdersServiceSqs}")
    fun listen(message: String) {
        val event = objectMapper.readValue(message, OrderEvent::class.java)
        eventHandlerProxy.handleEvent(event)
    }
}
