package com.ghrer.commerce.events.inventory

import com.ghrer.commerce.events.CommerceEvent
import com.ghrer.commerce.events.CommerceEventPublisher
import com.ghrer.commerce.events.inventory.model.InventoryEvent

interface InventoryEventPublisher : CommerceEventPublisher {

    override fun doesSupport(event: CommerceEvent) = event is InventoryEvent
}
