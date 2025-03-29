package com.ghrer.commerce.events.inventory.model

data class ItemAddedEvent(
    override val itemId: String,
    override val eventType: String = InventoryEventType.ITEM_ADDED.name,
    val quantity: Int,
) : InventoryEvent(itemId)
