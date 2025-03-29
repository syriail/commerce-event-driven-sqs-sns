package com.ghrer.commerce.events

import java.time.Instant

enum class CommerceEventTopic {
    ORDERS_TOPIC,
    INVENTORY_TOPIC
}

interface CommerceEvent {
    val topic: CommerceEventTopic
    val eventType: String
    val timestamp: Instant
    val eventGroupId: String
}
