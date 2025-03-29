package com.ghrer.commerce.events

interface CommerceEventPublisher {
    fun publish(event: CommerceEvent)

    fun doesSupport(event: CommerceEvent): Boolean
}
