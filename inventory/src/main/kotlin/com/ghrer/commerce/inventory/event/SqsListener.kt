package com.ghrer.commerce.inventory.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.ghrer.commerce.events.CommerceEvent
import com.ghrer.commerce.inventory.event.handler.EventHandlerProxy
import io.awspring.cloud.sqs.annotation.SqsListener
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("!test")
class SqsListener(
    private val objectMapper: ObjectMapper,
    private val eventHandlerProxy: EventHandlerProxy
) {

    @SqsListener("\${aws-resources.ordersTopicInventoryServiceSqs}")
    fun listen(message: String) {
        val event = objectMapper.readValue(message, CommerceEvent::class.java)
        eventHandlerProxy.handleEvent(event)
    }
}
