package com.ghrer.commerce.events

import io.awspring.cloud.sns.core.SnsHeaders
import io.awspring.cloud.sns.core.SnsTemplate
import mu.KotlinLogging

abstract class SnsEventPublisher(
    private val snsTemplate: SnsTemplate,
    private val snsTopic: String
) : CommerceEventPublisher {

    private val logger = KotlinLogging.logger { }

    override fun publish(event: CommerceEvent) {
        logger.debug { "Publishing the event: $event to $snsTopic" }
        snsTemplate.convertAndSend(
            snsTopic,
            event,
            mapOf(
                SnsHeaders.MESSAGE_GROUP_ID_HEADER to event.eventGroupId,
                SnsHeaders.MESSAGE_DEDUPLICATION_ID_HEADER to event.hashCode().toString(),
                EVENT_TYPE_HEADER to event.eventType

            )
        )
    }
    companion object {
        private const val EVENT_TYPE_HEADER = "eventType"
    }
}
