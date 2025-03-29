package com.ghrer.commerce.payments.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.ghrer.commerce.events.orders.model.OrderEvent
import com.ghrer.commerce.payments.event.handler.EventHandlerProxy
import io.awspring.cloud.sqs.annotation.SqsListener
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("!test")
class SqsListener(
    private val objectMapper: ObjectMapper,
    private val eventHandlerProxy: EventHandlerProxy
) {

    @SqsListener("\${aws-resources.ordersTopicPaymentsServiceSqs}")
    fun listen(message: String) {
        val event = objectMapper.readValue(message, OrderEvent::class.java)
        eventHandlerProxy.handleEvent(event)
    }
}
