package com.ghrer.commerce.events

interface EventPublisherProxy {
    fun publish(event: CommerceEvent)
}

class EventPublisherProxyImpl(
    private val publishers: List<CommerceEventPublisher>
) : EventPublisherProxy {

    override fun publish(event: CommerceEvent) {
        val publisher = publishers.firstOrNull { it.doesSupport(event) }
        checkNotNull(publisher) {
            "No publisher found for event ${event::class.simpleName}"
        }
        publisher.publish(event)
    }
}
