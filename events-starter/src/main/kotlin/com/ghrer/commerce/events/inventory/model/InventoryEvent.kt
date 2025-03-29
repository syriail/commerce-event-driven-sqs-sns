package com.ghrer.commerce.events.inventory.model

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.ghrer.commerce.events.CommerceEvent
import com.ghrer.commerce.events.CommerceEventTopic
import java.time.Instant

enum class InventoryEventType {
    ITEM_ADDED
}

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "eventType")
@JsonSubTypes(
    JsonSubTypes.Type(value = ItemAddedEvent::class, name = "ITEM_ADDED"),
)
abstract class InventoryEvent(
    open val itemId: String,
    override val topic: CommerceEventTopic = CommerceEventTopic.INVENTORY_TOPIC,
    override val timestamp: Instant = Instant.now(),
) : CommerceEvent {
    override val eventGroupId: String
        get() = itemId
}
