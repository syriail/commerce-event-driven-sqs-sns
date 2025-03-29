package com.ghrer.commerce.inventory.event.handler

import com.ghrer.commerce.events.CommerceEvent
import com.ghrer.commerce.events.orders.model.OrderCreatedEvent
import com.ghrer.commerce.inventory.business.InventoryProcessor
import com.ghrer.commerce.inventory.controller.dto.ReserveItemRequest
import org.springframework.stereotype.Service
import java.util.UUID
import kotlin.reflect.KClass

@Service
class OrderCreatedEventHandler(
    private val inventoryProcessor: InventoryProcessor
) : EventHandler {
    override fun handleEvent(event: CommerceEvent) {
        val orderCreatedEvent = event as OrderCreatedEvent
        inventoryProcessor.commitReservedItems(
            orderCreatedEvent.items.map {
                ReserveItemRequest(
                    UUID.fromString(it.id),
                    it.quantity
                )
            }
        ).block()
    }

    override fun getSupportedClass(): KClass<OrderCreatedEvent> {
        return OrderCreatedEvent::class
    }
}
